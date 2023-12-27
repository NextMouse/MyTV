package net.tv.util;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class GIFResizer {

    public static ImageIcon resize(ImageIcon inputImageIcon, Dimension targetSize) {
        try {
            return conversion(inputImageIcon, targetSize);
        } catch (Exception ex) {
            return inputImageIcon;
        }
    }

    private static ImageIcon conversion(ImageIcon inputImageIcon, Dimension targetSize) throws Exception {
        // 加载 GIF 动图
        Image inputImage = inputImageIcon.getImage();
        BufferedImage originalImage = new BufferedImage(inputImage.getWidth(null), inputImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = originalImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, null);
        graphics2D.dispose();

        // 缩小 GIF 动图
        BufferedImage resizedImage = new BufferedImage(targetSize.width, targetSize.height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetSize.width, targetSize.height, null);
        graphics2D.dispose();

        // 保存缩小后的 GIF 动图
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(inputImageIcon.toString().getBytes()));
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(imageInputStream);
        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
        writer.setOutput(new MemoryCacheImageOutputStream(outputStream));
        writer.prepareWriteSequence(metadata);

        for (int i = 0; i < reader.getNumImages(true); i++) {
            BufferedImage resizedFrame = resizedImage.getSubimage(0, 0, targetSize.width, targetSize.height);

            IIOMetadataNode rootNode = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1");
            IIOMetadataNode graphicControlExtensionNode = (IIOMetadataNode) rootNode.getElementsByTagName("GraphicControlExtension").item(0);

            IIOMetadataNode imageDescriptorNode = getChildNode(rootNode);
            imageDescriptorNode.setAttribute("Width", Integer.toString(targetSize.width));
            imageDescriptorNode.setAttribute("Height", Integer.toString(targetSize.height));

            IIOImage image = new IIOImage(resizedFrame, null, metadata);
            IIOMetadata imageMetadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            imageMetadata.mergeTree("javax_imageio_gif_image_1", rootNode);
            imageMetadata.mergeTree("javax_imageio_gif_graphics_control_extension", graphicControlExtensionNode);
            writer.writeToSequence(image, writeParam);
        }

        writer.endWriteSequence();
        writer.dispose();
        outputStream.close();
        reader.dispose();

        // 转换为 ImageIcon 对象并返回
        return new ImageIcon(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())));
    }

    private static final String NODE_NAME = "ImageDescriptor";

    private static IIOMetadataNode getChildNode(IIOMetadataNode rootNode) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(NODE_NAME)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(NODE_NAME);
        rootNode.appendChild(node);
        return node;
    }
}