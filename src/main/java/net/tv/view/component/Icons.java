package net.tv.view.component;

import cn.hutool.core.io.resource.ResourceUtil;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.apache.batik.transcoder.XMLAbstractTranscoder.*;
import static org.apache.batik.util.SVGConstants.SVG_NAMESPACE_URI;
import static org.apache.batik.util.SVGConstants.SVG_SVG_TAG;

public interface Icons {

    interface R {
        String PNG = ".png";
        String SVG = ".svg";
    }

    String LOGO_PATH = "icons/logo.png";
    String LOADING_PATH = "icons/loading.gif";

    ImageIcon TITLE_LOGO = loadImage(LOGO_PATH, new Dimension(22, 22));
    ImageIcon LOGO = new ImageIcon(ResourceUtil.getResource(LOGO_PATH));
    ImageIcon DEFAULT_TV_LOGO = loadImage(LOGO_PATH, new Dimension(80, 80));
    ImageIcon LOADING_TV_LOGO = new ImageIcon(ResourceUtil.getResource(LOADING_PATH));

    interface Standard {
        Dimension SIZE = new Dimension(22, 22);
        ImageIcon SIZE_MIN = loadImage("icons/Minimize-1.svg", SIZE);
        ImageIcon SIZE_MAX = loadImage("icons/Maximize-3.svg", SIZE);
        ImageIcon SIZE_NOT_MAX = loadImage("icons/Maximize-1.svg", SIZE);
        ImageIcon CLOSE = loadImage("icons/Shut down-1.svg", SIZE);
        ImageIcon EXPORT = loadImage("icons/收件下载_inbox-in.svg", SIZE);
        ImageIcon FILE_OPEN = loadImage("icons/视频文件_video-file.svg", SIZE);
        ImageIcon FILE_SAVE = loadImage("icons/保存_save.svg", SIZE);
        ImageIcon FILE_DELETE = loadImage("icons/删除_delete-four.svg", SIZE);
        ImageIcon VIDEO_PLAY = loadImage("icons/播放_play.svg", SIZE);
        ImageIcon VIDEO_PAUSE = loadImage("icons/暂停_pause-one.svg", SIZE);
        ImageIcon VIDEO_REFRESH = loadImage("icons/刷新_refresh.svg", SIZE);
        ImageIcon VIDEO_VOLUME_OPEN = loadImage("icons/声音-大_volume-notice.svg", SIZE);
        ImageIcon VIDEO_VOLUME_CLOSE = loadImage("icons/静音_volume-mute.svg", SIZE);
        ImageIcon VIDEO = loadImage("icons/视频_video.svg", SIZE);
        ImageIcon LIKE = loadImage("icons/喜欢_like.svg", SIZE);
        ImageIcon DISLIKE = loadImage("icons/不喜欢_dislike-two.svg", SIZE);
        ImageIcon FACE_HAPPY = loadImage("icons/开心_emotion-happy.svg", SIZE);
        ImageIcon FACE_CLEAR = loadImage("icons/格式刷_format-brush.svg", SIZE);
        ImageIcon LIGHTNING = loadImage("icons/闪电_lightning.svg", SIZE);
        ImageIcon CONFIG = loadImage("icons/设置_setting-two.svg", SIZE);
        ImageIcon DISTINCT = loadImage("icons/清除格式_clear-format.svg", SIZE);
        ImageIcon SEARCH = loadImage("icons/搜索_search.svg", SIZE);
    }

    interface MIN {
        Dimension SIZE = new Dimension(20, 20);
        ImageIcon LOAD = loadImage("icons/下载_download-four.svg", SIZE);
    }

    static ImageIcon loadImage(String url, Dimension size) {
        if (url.endsWith(R.SVG)) {
            return loadSVGImage(url, size);
        }
        return loadPNGImage(url, size);
    }

    static ImageIcon loadPNGImage(String url, Dimension size) {
        ImageIcon imageIcon = new ImageIcon(ResourceUtil.getResource(url));
        Image newImage = imageIcon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    static ImageIcon loadSVGImage(String url, Dimension size) {
        TranscodingHints hints = new TranscodingHints();
        hints.put(KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
        hints.put(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVG_NAMESPACE_URI);
        hints.put(KEY_DOCUMENT_ELEMENT, SVG_SVG_TAG);

        SvgTranscoder transcoder = new SvgTranscoder();
        transcoder.setTranscodingHints(hints);
        try {
            TranscoderInput input = new TranscoderInput(ResourceUtil.getStream(url));
            transcoder.transcode(input, null);
        } catch (TranscoderException e) {
            throw new RuntimeException(e);
        }
        ImageIcon imageIcon = new ImageIcon(transcoder.getImage());
        Image newImage = imageIcon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    class SvgTranscoder extends ImageTranscoder {

        private BufferedImage image = null;

        @Override
        public BufferedImage createImage(int width, int height) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            return image;
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput out) {
        }

        BufferedImage getImage() {
            return image;
        }

    }

}
