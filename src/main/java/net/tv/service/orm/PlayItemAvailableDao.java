package net.tv.service.orm;

import cn.hutool.core.util.StrUtil;
import net.tv.service.model.PlayItemAvailable;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayItemAvailableDao {

    private Connection getConnection() {
        return SqliteHelper.get()
                           .getConnection(SqliteHelper.Type.TvDB);
    }

    interface SQL {
        String TABLE_NAME = "tb_play_item_available";
        String SELECT_ALL = "SELECT id, mediaUrl FROM `" + TABLE_NAME + "`";
        String SELECT_BY_ID = "SELECT id, mediaUrl FROM `" + TABLE_NAME + "` WHERE id=?";
        String SELECT_BY_MEDIA_URL = "SELECT id, mediaUrl FROM `" + TABLE_NAME + "` WHERE mediaUrl=?";
        String DELETE = "DELETE FROM `" + TABLE_NAME + "` WHERE id=?";
        String INSERT = "INSERT INTO `" + TABLE_NAME + "` (id, mediaUrl) VALUES (?,?)";
    }

    public void insert(PlayItemAvailable playItemAvailable) throws SQLException {
        if (playItemAvailable == null) return;
        Object[] objectParams = new Object[2];
        objectParams[0] = playItemAvailable.getId();
        objectParams[1] = playItemAvailable.getMediaUrl();
        QueryRunner runner = new QueryRunner();
        runner.update(getConnection(), SQL.INSERT, objectParams);
    }

    public void delete(PlayItemAvailable playItemAvailable) throws SQLException {
        if (playItemAvailable.getId() == null) return;
        QueryRunner runner = new QueryRunner();
        runner.update(getConnection(), SQL.DELETE, playItemAvailable.getId());
    }

    public PlayItemAvailable selectByMediaUrl(String mediaUrl) throws SQLException {
        if (StrUtil.isBlank(mediaUrl)) return null;
        QueryRunner runner = new QueryRunner();
        Object[] objectParams = {mediaUrl};
        return runner.query(getConnection(), SQL.SELECT_BY_MEDIA_URL, resultSet -> {
            final String rowId = resultSet.getString("id");
            final String rowMediaUrl = resultSet.getString("mediaUrl");
            return StrUtil.isNotBlank(rowId) ? new PlayItemAvailable(rowId, rowMediaUrl) : null;
        }, objectParams);
    }

    public PlayItemAvailable selectById(String id) throws SQLException {
        if (StrUtil.isBlank(id)) return null;
        QueryRunner runner = new QueryRunner();
        Object[] objectParams = {id};
        return runner.query(getConnection(), SQL.SELECT_BY_ID, resultSet -> {
            final String rowId = resultSet.getString("id");
            final String rowMediaUrl = resultSet.getString("mediaUrl");
            return StrUtil.isNotBlank(rowId) ? new PlayItemAvailable(rowId, rowMediaUrl) : null;
        }, objectParams);
    }

    public List<PlayItemAvailable> select() throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(getConnection(), SQL.SELECT_ALL, new AbstractListHandler<>() {
            @Override
            protected PlayItemAvailable handleRow(ResultSet resultSet) throws SQLException {
                final String rowId = resultSet.getString("id");
                final String rowMediaUrl = resultSet.getString("mediaUrl");
                return new PlayItemAvailable(rowId, rowMediaUrl);
            }
        });
    }

}
