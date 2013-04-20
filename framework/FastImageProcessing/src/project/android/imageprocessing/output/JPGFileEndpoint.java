package project.android.imageprocessing.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

/**
 * A JPG image renderer extension of GLRenderer. 
 * This class accepts a texture as input and renders it to a jpeg image file.
 * Images will be saved to file every time the texture is updated.  If increment is on, it will save the file with the 
 * given base name and an added number in the format "filePath%d.jpg".  The filepath is the path and name of the image 
 * file that should be written to (".jpg" will be appened automatically).  This class does not
 * handle displaying to the screen; however it does use the screen to render to the video recorder, so if display is not
 * required the opengl context should be hidden.
 * @author Chris Batt
 */
public class JPGFileEndpoint extends GLRenderer implements GLTextureInputRenderer{
	private String filePath;
	private boolean increment;
	private int curNumber;
	
	/**
	 * @param filePath
	 * The file path and name of the file that the image should be written to.
	 * @param increment
	 * Whether or not a new image should be written for each input change.
	 */
	public JPGFileEndpoint(String filePath, boolean increment) {
		this.filePath = filePath;
		this.increment = increment;
		curNumber = 1;
		rotateClockwise90Degrees(2);
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		texture_in = texture;
		width = source.getWidth();
		height = source.getHeight();
		onDrawFrame();
		int[] pixels = new int[width*height];
		IntBuffer intBuffer = IntBuffer.wrap(pixels);
		intBuffer.position(0);
		GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = (pixels[i] & (0xFF00FF00)) | ((pixels[i] >> 16) & 0x000000FF) | ((pixels[i] << 16) & 0x00FF0000); //swap red and blue to translate back to bitmap rgb style
		}
		Bitmap image = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
		String filePathName;
		if(increment) {
			filePathName = filePath+curNumber+".jpg";
			curNumber++;
		} else {
			filePathName = filePath+".jpg";
		}
		try {
			OutputStream out = new FileOutputStream(new File(filePathName));
			image.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}