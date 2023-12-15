package net.tv.m3u;

public interface Constants {

    /**
     * comments
     */
    String COMMENT = "#";
    /**
     * #EXTM3U At the VERY top of the Extended M3U file is this header, which signifies this is an extended M3U file.
     * "#EXTM3U" must be all capital letters.
     */
    static String EXTM3U = "#EXTM3U";
    /**
     * This signifies this is an Extended Information field.
     */
    String EXTINF = "#EXTINF:";

    /**
     * defines the background of the tile related to described playlist's item.
     * After this directive may be written url to the image, or the color in #rrggbb or rgba
     */
    String EXTBG = "#EXTBG:";

    /**
     * album information, title in particular
     */
    String EXTLAB = "#EXTALB:";

    /**
     * defines the size of the tile related to described playlist's item.
     * Available values: small, medium, big
     */
    String EXTSIZE = "#EXTSIZE:";

    /**
     * after the directive may be written url that has to be requested when the tile will be pressed.
     * The request is sent before content uploading.
     * This function may be useful for receiver' owners as the app may to send to receiver the command to change channel.
     */
    String EXTCTRL = "#EXTCTRL:";

}
