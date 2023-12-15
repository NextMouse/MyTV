package net.tv.service.orm;

import cn.hutool.core.collection.CollectionUtil;
import net.tv.service.model.PlayViewItem;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayViewItemDao {

    private Connection getConnection() {
        return SqliteHelper.get().getConnection(SqliteHelper.Type.TempDB);
    }

    interface SQL {
        String TABLE_NAME = "tb_play_item";
        String PARAMS_NAME = "groupTitle,channelTitle,duration,tvgId,tvgName,tvgLogo,aspectRatio,mediaUrl";
        String INSERT = "INSERT INTO `" + TABLE_NAME + "` (" + PARAMS_NAME + ",id) VALUES (?,?,?,?,?,?,?,?,?) ";
        String UPDATE = "UPDATE `" + TABLE_NAME + "` SET groupTitle=?,channelTitle=?,duration=?,tvgId=?,tvgName=?,tvgLogo=?,aspectRatio=?,mediaUrl=? WHERE id = ? ";
        String UPDATE_GROUP_TITLE = "UPDATE `" + TABLE_NAME + "` SET groupTitle=? WHERE groupTitle = ? ";
        String DELETE = "DELETE FROM `" + TABLE_NAME + "` WHERE id=?";
        String TRUNCATE = "DELETE FROM `" + TABLE_NAME + "` ";
        String SELECT_BY_GROUP_TITLE = "SELECT id," + PARAMS_NAME + "  FROM `" + TABLE_NAME + "` WHERE groupTitle=?";
        String SELECT_GROUP = "SELECT DISTINCT groupTitle FROM `" + TABLE_NAME + "`";
        String SELECT_ALL = "SELECT id," + PARAMS_NAME + " FROM `" + TABLE_NAME + "`";
    }

    public void batchInsert(List<PlayViewItem> playViewItemList) throws SQLException {
        if (CollectionUtil.isEmpty(playViewItemList)) return;
        getConnection().setAutoCommit(false);
        QueryRunner runner = new QueryRunner();
        Object[][] objects = new Object[playViewItemList.size()][];
        for (int i = 0; i < playViewItemList.size(); i++) {
            PlayViewItem playViewItem = playViewItemList.get(i);
            objects[i] = getObjectParams(playViewItem);
        }
        runner.batch(getConnection(), SQL.INSERT, objects);
        SqliteHelper.get().getConnection(SqliteHelper.Type.TempDB).commit();
    }

    private Object[] getObjectParams(PlayViewItem playViewItem) {
        Object[] objectParams = new Object[9];
        objectParams[0] = playViewItem.getGroupTitle();
        objectParams[1] = playViewItem.getChannelTitle();
        objectParams[2] = playViewItem.getDuration();
        objectParams[3] = playViewItem.getTvgId();
        objectParams[4] = playViewItem.getTvgName();
        objectParams[5] = playViewItem.getTvgLogo();
        objectParams[6] = playViewItem.getAspectRatio();
        objectParams[7] = playViewItem.getMediaUrl();
        objectParams[8] = playViewItem.getId();
        return objectParams;
    }

    public void insert(PlayViewItem playViewItem) throws SQLException {
        if (playViewItem == null) return;
        QueryRunner runner = new QueryRunner();
        runner.update(getConnection(), SQL.INSERT, getObjectParams(playViewItem));
    }

    public void update(PlayViewItem playViewItem) throws SQLException {
        if (playViewItem.getId() == null) return;
        QueryRunner runner = new QueryRunner();
        runner.update(getConnection(), SQL.UPDATE, getObjectParams(playViewItem));
    }

    public int updateGroupTitle(String oldGroupTitle, String newGroupTitle) throws SQLException {
        assert oldGroupTitle != null;
        assert newGroupTitle != null;
        QueryRunner runner = new QueryRunner();
        Object[] objectParams = {newGroupTitle, oldGroupTitle};
        return runner.update(getConnection(), SQL.UPDATE_GROUP_TITLE, objectParams);
    }

    public void delete(PlayViewItem playViewItem) throws SQLException {
        if (playViewItem.getId() == null) return;
        QueryRunner runner = new QueryRunner();
        runner.update(getConnection(), SQL.DELETE, playViewItem.getId());
    }

    public List<String> queryGroupTitleList() throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        return queryRunner.query(getConnection(), SQL.SELECT_GROUP, new AbstractListHandler<String>() {
            @Override
            protected String handleRow(ResultSet resultSet) throws SQLException {
                return resultSet.getString("groupTitle");
            }
        });
    }

    public List<PlayViewItem> queryByGroupTitle(String groupTitle) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        return queryRunner.query(getConnection(), SQL.SELECT_BY_GROUP_TITLE, resultListHandler, groupTitle);
    }

    private AbstractListHandler<PlayViewItem> resultListHandler = new AbstractListHandler<>() {
        @Override
        protected PlayViewItem handleRow(ResultSet resultSet) throws SQLException {
            return PlayViewItem.builder().id(resultSet.getString("id")).groupTitle(resultSet.getString("groupTitle")).channelTitle(resultSet.getString("channelTitle")).duration(resultSet.getInt("duration")).tvgId(resultSet.getString("tvgId")).tvgName(resultSet.getString("tvgName")).tvgLogo(resultSet.getString("tvgLogo")).aspectRatio(resultSet.getString("aspectRatio")).mediaUrl(resultSet.getString("mediaUrl")).favorite(false).build();
        }
    };

    public List<PlayViewItem> queryAll() throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        return queryRunner.query(getConnection(), SQL.SELECT_ALL, resultListHandler);
    }

    public void truncate() throws SQLException {
        new QueryRunner().update(getConnection(), SQL.TRUNCATE);
    }


}
