package net.tv.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomizeList<T extends JComponent> extends JList<T> {

    public void clear() {
        listModel.clear();
        setModel(listModel);
    }

    private int currentIndex;

    private final DefaultListModel<T> listModel = new DefaultListModel<>();

    public void setData(List<T> data) {
        listModel.clear();
        listModel.addAll(data);
        setModel(listModel);
    }

    public T getModel(int index) {
        return listModel.get(index);
    }

    public <P> void setData(List<P> data, Function<P, T> function) {
        listModel.clear();
        data.forEach(e -> listModel.addElement(function.apply(e)));
        setModel(listModel);
    }

    private final CustomizeComponent customize;

    public CustomizeList(CustomizeComponent customize, Consumer<T> consumer) {
        final CustomizeList customizeList = this;
        this.customize = customize;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (consumer != null) {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (getSelectedValue() != null) {
                        consumer.accept(getSelectedValue());
                    }
                }
            });
        }
        setBackground(null);
        setCellRenderer(new CustomizeItemRenderer());
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    currentIndex = locationToIndex(e.getPoint());
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                currentIndex = -1;
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON3 == e.getButton()) {
                    if (mouseEventConsumer != null) {
                        try {
                            int index = locationToIndex(e.getPoint());
                            if (index == -1) return;
                            mouseEventConsumer.accept(e, getModel(index));
                        } catch (Exception ex) {
                            // ignore
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentIndex = -1;
            }
        });
    }

    private java.util.function.BiConsumer<MouseEvent, T> mouseEventConsumer;

    public void setMouseRightClickEvent(BiConsumer<MouseEvent, T> mouseEventConsumer) {
        this.mouseEventConsumer = mouseEventConsumer;
    }

    public class CustomizeItemRenderer implements ListCellRenderer<T> {

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
            customize.onInitialize(list, value);
            if (currentIndex == index) {
                customize.onMouseMoved(list, value);
            } else if (isSelected) {
                customize.onSelected(list, value);
            } else {
                customize.onDefault(list, value);
            }
            return value;
        }
    }


}
