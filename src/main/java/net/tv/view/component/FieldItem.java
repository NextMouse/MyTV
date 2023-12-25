package net.tv.view.component;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class FieldItem extends JPanel {

    private final JLabel fieldTitle;


    private final JTextField textField;

    public FieldItem(String title) {
        fieldTitle = new JLabel(" " + title + "：");
        textField = new JTextField();

        setLayout(new BorderLayout());

        add(fieldTitle, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
    }

    public void setFieldValue(String value) {
        textField.setText(value);
        textField.setCaretPosition(0);
    }

    public String getFieldValue() {
        return textField.getText();
    }

}
