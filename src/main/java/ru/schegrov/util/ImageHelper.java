package ru.schegrov.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;

/**
 * Created by ramon on 08.09.2016.
 */
public class ImageHelper {

    private static final Logger logger = Logger.getLogger(ImageHelper.class);
    public static ImageView loadImage(String path) {
        logger.trace("image: " + path);
        ImageView view = null;
        try {
            view = new ImageView(new Image(ImageHelper.class.getResourceAsStream(path)));
//        if (isLittleImage()){
            view.setFitHeight(16);
            view.setFitWidth(16);
//        }
            logger.trace("image found");
        } catch (Exception e) {
            logger.error("loadImage error", e);
        }
        return view;
    }
}
