package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.TwoPassMultiPixelFilter;
import android.opengl.GLES20;

/**
 * A bilateral blur, which tries to blur similar color values while preserving sharp edges
 * blurSize: A multiplier for the size of the blur, ranging from 0.0 on up
 * distanceNormalizationFactor: A normalization factor for the distance between central color and sample color
 * @author Chris Batt
 */
public class BilateralBlurFilter extends TwoPassMultiPixelFilter {
	private static final String UNIFORM_DISTANCE_NORMALIZATION = "u_DistanceNormalization";
	
	private int distanceNormalizationHandle;
	private float distanceNormalization;
	
	public BilateralBlurFilter(float distanceNormalizationFactor) {
		this.distanceNormalization = distanceNormalizationFactor;
	}
			
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_DISTANCE_NORMALIZATION+";\n"
				+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
				+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
						
				
		  		+"void main(){\n"
		  		
		  		+"   vec2 singleStepOffset = vec2("+UNIFORM_TEXELWIDTH+", "+UNIFORM_TEXELHEIGHT+");\n"
				+"   int multiplier = 0;\n"
				+"   vec2 blurStep = vec2(0,0);\n"
				+"   vec2 blurCoordinates[9];"
				+"   for(int i = 0; i < 9; i++) {\n"
				+"     multiplier = (i - 4);\n"
				+"     blurStep = float(multiplier) * singleStepOffset;\n"
				+"     blurCoordinates[i] = "+VARYING_TEXCOORD+".xy + blurStep;\n"
				+"   }\n"
				
				+"   vec4 centralColor;\n"
				+"   float gaussianWeightTotal;\n"
				+"   vec4 sum;\n"
				+"   vec4 sampleColor;\n"
				+"   float distanceFromCentralColor;\n"
				+"   float gaussianWeight;\n"
				     
				+"   centralColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[4]);\n"
				+"   gaussianWeightTotal = 0.18;\n"
				+"   sum = centralColor * 0.18;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[0]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[1]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[2]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[3]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[5]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[6]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[7]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[8]);\n"
				+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
				+"   gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n"
				+"   gaussianWeightTotal += gaussianWeight;\n"
				+"   sum += sampleColor * gaussianWeight;\n"
				     
				+"   gl_FragColor = sum / gaussianWeightTotal;\n"
				+"}\n";
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		distanceNormalizationHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_DISTANCE_NORMALIZATION);
	} 
		
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(distanceNormalizationHandle, distanceNormalization);
	}
}
