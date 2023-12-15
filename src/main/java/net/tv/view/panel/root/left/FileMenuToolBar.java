package net.tv.view.panel.root.left;

import cn.hutool.core.collection.CollectionUtil;
import net.tv.service.PlaylistService;
import net.tv.service.orm.SqliteHelper;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.config.SystemConfig;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileMenuToolBar extends JToolBar {

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 3);
    }

    private final java.util.List<JComponent> componentList = new ArrayList<>();

    {
        componentList.add(new SimpleButton("打开", Icons.Standard.FILE_OPEN, (e, btn) -> {
            SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
            // 加载
            JFileChooser fileChooser = new JFileChooser(systemConfig.getOpenDirPath());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
                File file = fileChooser.getSelectedFile();
                systemConfig.setOpenDirPath(file.getParent());
                systemConfig.save();
                GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, service -> service.readFile(file));
                showGroupPanel();
            }
        }));

        componentList.add(new SimpleButton("导出", Icons.Standard.EXPORT, (e, btn) -> {
            SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
            JFileChooser chooser = new JFileChooser(systemConfig.getExportDirPath());
            chooser.setFileFilter(new FileNameExtensionFilter("(*.m3u)", "m3u"));
            int option = chooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
                try {
                    File selectedFile = chooser.getSelectedFile();
                    systemConfig.setExportDirPath(selectedFile.getParent());
                    systemConfig.save();
                    String fileName = chooser.getName(selectedFile);    //从文件名输入框中获取文件名
                    //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
                    if (!fileName.contains(".") && !fileName.contains(".m3u")) {
                        selectedFile = new File(chooser.getCurrentDirectory(), fileName + ".m3u");
                    }
                    final File saveFile = selectedFile;
                    GodHand.<PlaylistService>exec(GodHand.K.PlaylistService,
                            playListService -> playListService.saveToFile(saveFile));
                } catch (Exception ex) {
                    ConsoleLog.println(ex);
                }
            }

        }));

        componentList.add(new SimpleButton("清空", Icons.Standard.FACE_CLEAR, (e, btn) -> {
            try {
                GodHand.exec(GodHand.K.GroupListPanel, GroupListPanel::clear);
                GodHand.exec(GodHand.K.GroupItemListPanel, GroupItemListPanel::clear);
                SqliteHelper.get()
                        .close();
            } catch (Exception ex) {
                ConsoleLog.println(ex);
            }
        }));

        componentList.add(new SimpleButton("去重", Icons.Standard.DISTINCT, (e, btn) -> {
            try {
                GodHand.exec(GodHand.K.PlaylistService, PlaylistService::distinct);
                showGroupPanel();
            } catch (Exception ex) {
                ConsoleLog.println(ex);
            }
        }));

    }

    public void showGroupPanel() {
        GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, service -> {
            List<String> groupTitleList = service.getByGroupTitle();
            if (CollectionUtil.isNotEmpty(groupTitleList)) {
                GodHand.<GroupListPanel>exec(GodHand.K.GroupListPanel, groupListPanel -> {
                    if (groupListPanel.customizeList.getSelectedValue() != null) {
                        groupListPanel.refresh(groupListPanel.customizeList.getSelectedValue().getTrimText());
                    } else {
                        groupListPanel.refresh(null);
                    }
                });
            }
        });
    }

    public FileMenuToolBar() {
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        setLayout(flowLayout);
        setMargin(R.MARGIN);
        setFloatable(false);
        for (JComponent component : componentList) {
            add(component);
        }
    }

}
