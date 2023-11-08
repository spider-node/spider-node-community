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
package cn.spider.framework.transaction.sdk.datasource.undo;

import cn.spider.framework.transaction.sdk.datasource.sql.struct.Field;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.Row;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.TableRecords;
import cn.spider.framework.transaction.sdk.datasource.util.ColumnUtils;
import cn.spider.framework.transaction.sdk.datasource.util.JdbcConstants;
import cn.spider.framework.transaction.sdk.util.CollectionUtils;
import cn.spider.framework.transaction.sdk.core.exception.ShouldNeverHappenException;
import cn.spider.framework.transaction.sdk.datasource.SqlGenerateUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type My sql undo update executor.
 *
 * @author DDS
 */
public class MySQLUndoUpdateExecutor extends AbstractUndoExecutor {

    /**
     * UPDATE a SET x = ?, y = ?, z = ? WHERE pk1 in (?) pk2 in (?)
     */
    private static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE %s ";

    /**
     * Undo Update.
     *
     * @return sql
     */
    @Override
    protected String buildUndoSQL() {
        TableRecords beforeImage = sqlUndoLog.getBeforeImage();
        List<Row> beforeImageRows = beforeImage.getRows();
        if (CollectionUtils.isEmpty(beforeImageRows)) {
            throw new ShouldNeverHappenException("Invalid UNDO LOG"); // TODO
        }
        Row row = beforeImageRows.get(0);

        List<Field> nonPkFields = row.nonPrimaryKeys();
        // update sql undo log before image all field come from table meta. need add escape.
        // see BaseTransactionalExecutor#buildTableRecords
        String updateColumns = nonPkFields.stream().map(
            field -> ColumnUtils.addEscape(field.getName(), JdbcConstants.MYSQL) + " = ?").collect(
            Collectors.joining(", "));

        List<String> pkNameList = getOrderedPkList(beforeImage, row, JdbcConstants.MYSQL).stream().map(e -> e.getName())
            .collect(Collectors.toList());
        String whereSql = SqlGenerateUtils.buildWhereConditionByPKs(pkNameList, JdbcConstants.MYSQL);

        return String.format(UPDATE_SQL_TEMPLATE, sqlUndoLog.getTableName(), updateColumns, whereSql);
    }

    /**
     * Instantiates a new My sql undo update executor.
     *
     * @param sqlUndoLog the sql undo log
     */
    public MySQLUndoUpdateExecutor(SQLUndoLog sqlUndoLog) {
        super(sqlUndoLog);
    }

    @Override
    protected TableRecords getUndoRows() {
        return sqlUndoLog.getBeforeImage();
    }
}
