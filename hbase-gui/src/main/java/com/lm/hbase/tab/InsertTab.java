package com.lm.hbase.tab;

import com.lm.hbase.adapter.ColumnFamily;
import com.lm.hbase.adapter.ColumnFamilyParam;
import com.lm.hbase.adapter.entity.QualifierValue;
import com.lm.hbase.common.ImageIconConstons;
import com.lm.hbase.swing.HbaseGui;
import com.lm.hbase.swing.SwingConstants;
import com.lm.hbase.util.MyBytesUtil;
import com.lm.hbase.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: huangsn
 * Date: 2021/7/2
 * Time: 3:29 下午
 */
public class InsertTab extends TabAbstract {

    private JList<String> list = null;

    private JButton refreshTableButton;

    private JPanel insertPanel;

    private JLabel tableNameLabel;
    private JComboBox<String> cfComboBox;

    private JTextField rowKeyTextField;
    private JTextField cfTextField;
    private JTextField qualifierTextField;
    private JTextField valueTextField;
    private JButton insertButton;


    public InsertTab(HbaseGui window) {
        super(window);
    }

    @Override
    public JComponent initializePanel() {
        JSplitPane parentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 展示数据库列表的panel
        JPanel tableListPanel = new JPanel();
        tableListPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        tableListPanel.setLayout(new BorderLayout(1, 1));

        refreshTableButton = new JButton("刷新", ImageIconConstons.UPDATE_ICON);
        tableListPanel.add(refreshTableButton, BorderLayout.NORTH);

        list = new JList<>();
        list.setFixedCellHeight(20);
        list.setFixedCellWidth(250);
        // 设置为单选模式
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane jlistScroll = new JScrollPane(list);
        jlistScroll.setBorder(new TitledBorder("TABLES"));
        jlistScroll.setLayout(new ScrollPaneLayout());
        tableListPanel.add(jlistScroll);

        // 添加HbaseTableList Jlist所在的Panel到主容器中
        parentPanel.setLeftComponent(tableListPanel);

        // 展示表详情的父容器
        JSplitPane contentPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel contentChildPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setTopComponent(contentChildPanel);

        parentPanel.setRightComponent(contentPanel);

        insertPanel = new JPanel();
        insertPanel.setBorder(new TitledBorder("插入记录数据"));

        contentChildPanel.add(insertPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        insertPanel.setLayout(new GridBagLayout());

        {
            JLabel label = new JLabel("rowKey");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;// 横占X个单元格
            gbc.gridheight = 1;// 列占X个单元格
            gbc.weightx = 0;// 当窗口放大时，长度不变
            gbc.weighty = 0;// 当窗口放大时，高度不变
            insertPanel.add(label, gbc);
        }
        {
            rowKeyTextField = new JTextField(28);
            JPanel componentPanel = new JPanel();
            componentPanel.add(rowKeyTextField);

            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(componentPanel, gbc);
        }

        {
            JLabel label = new JLabel("cf");
            gbc.gridx = 6;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(label, gbc);
        }
        {
            cfTextField = new JTextField(28);
            //默认family列族为cf
            cfTextField.setText("cf");
            JPanel componentPanel = new JPanel();
            componentPanel.add(cfTextField);

            gbc.gridx = 8;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(componentPanel, gbc);
        }
        {
            JLabel label = new JLabel("qualifier");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(label, gbc);
        }
        {
            qualifierTextField = new JTextField(28);
            JPanel componentPanel = new JPanel();
            componentPanel.add(qualifierTextField);

            gbc.gridx = 3;
            gbc.gridy = 1;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(componentPanel, gbc);
        }
        {
            JLabel label = new JLabel("value");
            gbc.gridx = 6;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(label, gbc);
        }
        {
            valueTextField = new JTextField(28);
            JPanel componentPanel = new JPanel();
            componentPanel.add(valueTextField);

            gbc.gridx = 8;
            gbc.gridy = 1;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            insertPanel.add(componentPanel, gbc);
        }
        {
            insertButton = new JButton("插入");

            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;

            insertButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //获取表名
                    String table = list.getSelectedValue();
                    if (table == null || table.length() == 0) {
                        JOptionPane.showMessageDialog(getFrame(), "请选择左侧的表", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String rowKeyStr = rowKeyTextField.getText();
                    String cfStr = cfTextField.getText();
                    String qualifierStr = qualifierTextField.getText();
                    String value = valueTextField.getText();
                    try {
                       startTask();

                        getSingleThreadPool().execute(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    SwingConstants.hbaseAdapter.insertData(table, rowKeyStr, cfStr, qualifierStr, value.getBytes());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JOptionPane.showMessageDialog(getFrame(), "成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                                stopTask();
                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            insertPanel.add(insertButton, gbc);
        }
        // 初始化表
        try {
            initTableList(list);
        } catch (Exception e) {
            exceptionAlert(e);
        }

        return parentPanel;
    }

    @Override
    public String getTitle() {
        return "插入记录";
    }

    @Override
    public void enableAll() {
        list.setEnabled(true);
        refreshTableButton.setEnabled(true);
    }

    @Override
    public void disableAll() {
        list.setEnabled(false);
        refreshTableButton.setEnabled(false);
    }
}
