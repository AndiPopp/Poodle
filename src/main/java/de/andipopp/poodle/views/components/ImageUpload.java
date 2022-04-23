package de.andipopp.poodle.views.components;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.util.UUIDUtils;

public class ImageUpload extends Upload {

	public static final String FILE_EXTENSION = ".png";
	
	public static final String MIME_TYPE = "image/png";
	


	public ImageUpload(ImageReceiver receiver) {
		super(receiver);
		setAcceptedFileTypes(MIME_TYPE, FILE_EXTENSION);
		setMaxFileSize(Config.getCurrent().getImageSizeLimitKiloBytes() * 1024);
		
		addFileRejectedListener(event -> {
		    String errorMessage = event.getErrorMessage();

		    Notification notification = Notification.show(
		            errorMessage,
		            5000,
		            Notification.Position.MIDDLE
		    );
		    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
	}
	
	
	

	
	public static class ImageReceiver implements Receiver {

		String subFolder;
		
		UUID parentId;
		
		/**
		 * @param subFolder
		 * @param parentId
		 */
		public ImageReceiver(String subFolder, UUID parentId) {
			this.subFolder = subFolder;
			this.parentId = parentId;
		}


		@Override
		public OutputStream receiveUpload(String fileName, String mimeType) {
			try {
				return new FileOutputStream(subFolder 
						+ System.getProperty("file.separator") 
						+ UUIDUtils.uuidToBase64url(parentId)
						+ FILE_EXTENSION
				);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		
	}
	
}
