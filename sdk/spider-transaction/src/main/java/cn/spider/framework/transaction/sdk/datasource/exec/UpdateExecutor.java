/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.spider.framework.transaction.sdk.datasource.exec;

import cn.spider.framework.transaction.sdk.datasource.sql.struct.TableMeta;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.TableRecords;
import cn.spider.framework.transaction.sdk.datasource.util.ColumnUtils;
import cn.spider.framework.transaction.sdk.util.IOUtil;
import cn.spider.framework.transaction.sdk.datasource.SqlGenerateUtils;
import cn.spider.framework.transaction.sdk.datasource.StatementProxy;
import cn.spider.framework.transaction.sdk.sqlparser.SQLRecognizer;
import cn.spider.framework.transaction.sdk.sqlparser.SQLUpdateRecognizer;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * The type Update executor.
 *
 * @param <T> the type parameter
 * @param <S> the type parameter
 * @author DDS
 */
public class UpdateExecutor<T, S extends Statement> extends AbstractDMLBaseExecutor<T, S> {


    private static final boolean ONLY_CARE_UPDATE_COLUMNS =true;

    /**
     * Instantiates a new Update executor.
     *
     * @param statementProxy    the statement proxy
     * @param statementCallback the statement callback
     * @param sqlRecognizer     the sql recognizer
     */
    public UpdateExecutor(StatementProxy<S> statementProxy, StatementCallback<T, S> statementCallback,
                          SQLRecognizer sqlRecognizer) {
        super(statementProxy, statementCallback, sqlRecognizer);
    }

    @Override
    protected TableRecords beforeImage() throws SQLException {
        ArrayList<List<Object>> paramAppenderList = new ArrayList<>();
        TableMeta tmeta = getTableMeta();
        String selectSQL = buildBeforeImageSQL(tmeta, paramAppenderList);
        return buildTableRecords(tmeta, selectSQL, paramAppenderList);
    }

    private String buildBeforeImageSQL(TableMeta tableMeta, ArrayList<List<Object>> paramAppenderList) {
        SQLUpdateRecognizer recognizer = (SQLUpdateRecognizer) sqlRecognizer;
        List<String> updateColumns = recognizer.getUpdateColumns();
        StringBuilder prefix = new StringBuilder("SELECT ");
        StringBuilder suffix = new StringBuilder(" FROM ").append(getFromTableInSQL());
        String whereCondition = buildWhereCondition(recognizer, paramAppenderList);
        String orderByCondition = buildOrderCondition(recognizer, paramAppenderList);
        String limitCondition = buildLimitCondition(recognizer, paramAppenderList);
        if (StringUtils.isNotBlank(whereCondition)) {
            suffix.append(WHERE).append(whereCondition);
        }
        if (StringUtils.isNotBlank(orderByCondition)) {
            suffix.append(" ").append(orderByCondition);
        }
        if (StringUtils.isNotBlank(limitCondition)) {
            suffix.append(" ").append(limitCondition);
        }
        suffix.append(" FOR UPDATE");
        StringJoiner selectSQLJoin = new StringJoiner(", ", prefix.toString(), suffix.toString());
        if (ONLY_CARE_UPDATE_COLUMNS) {
            if (!containsPK(updateColumns)) {
                selectSQLJoin.add(getColumnNamesInSQL(tableMeta.getEscapePkNameList(getDbType())));
            }
            for (String columnName : updateColumns) {
                selectSQLJoin.add(columnName);
            }
        } else {
            for (String columnName : tableMeta.getAllColumns().keySet()) {
                selectSQLJoin.add(ColumnUtils.addEscape(columnName, getDbType()));
            }
        }
        return selectSQLJoin.toString();
    }

    @Override
    protected TableRecords afterImage(TableRecords beforeImage) throws SQLException {
        TableMeta tmeta = getTableMeta();
        if (beforeImage == null || beforeImage.size() == 0) {
            return TableRecords.empty(getTableMeta());
        }
        String selectSQL = buildAfterImageSQL(tmeta, beforeImage);
        ResultSet rs = null;
        try (PreparedStatement pst = statementProxy.getConnection().prepareStatement(selectSQL)) {
            SqlGenerateUtils.setParamForPk(beforeImage.pkRows(), getTableMeta().getPrimaryKeyOnlyName(), pst);
            rs = pst.executeQuery();
            return TableRecords.buildRecords(tmeta, rs);
        } finally {
            IOUtil.close(rs);
        }
    }

    private String buildAfterImageSQL(TableMeta tableMeta, TableRecords beforeImage) throws SQLException {
        StringBuilder prefix = new StringBuilder("SELECT ");
        String whereSql = SqlGenerateUtils.buildWhereConditionByPKs(tableMeta.getPrimaryKeyOnlyName(), beforeImage.pkRows().size(), getDbType());
        String suffix = " FROM " + getFromTableInSQL() + " WHERE " + whereSql;
        StringJoiner selectSQLJoiner = new StringJoiner(", ", prefix.toString(), suffix);
        if (ONLY_CARE_UPDATE_COLUMNS) {
            SQLUpdateRecognizer recognizer = (SQLUpdateRecognizer) sqlRecognizer;
            List<String> updateColumns = recognizer.getUpdateColumns();
            if (!containsPK(updateColumns)) {
                selectSQLJoiner.add(getColumnNamesInSQL(tableMeta.getEscapePkNameList(getDbType())));
            }
            for (String columnName : updateColumns) {
                selectSQLJoiner.add(columnName);
            }
        } else {
            for (String columnName : tableMeta.getAllColumns().keySet()) {
                selectSQLJoiner.add(ColumnUtils.addEscape(columnName, getDbType()));
            }
        }
        return selectSQLJoiner.toString();
    }

}
