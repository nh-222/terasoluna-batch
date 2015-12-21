/*
 * Copyright (c) 2015 NTT DATA Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.terasoluna.fw.batch.executor.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.util.Assert;

import jp.terasoluna.fw.batch.constants.JobStatusConstants;
import jp.terasoluna.fw.batch.constants.LogId;
import jp.terasoluna.fw.batch.executor.controller.AsyncJobOperatorImpl;
import jp.terasoluna.fw.batch.executor.dao.SystemDao;
import jp.terasoluna.fw.batch.executor.vo.BatchJobData;
import jp.terasoluna.fw.batch.executor.vo.BatchJobListParam;
import jp.terasoluna.fw.batch.executor.vo.BatchJobListResult;
import jp.terasoluna.fw.batch.executor.vo.BatchJobManagementParam;
import jp.terasoluna.fw.logger.TLogger;

/**
 * ジョブパラメータ解決の実装クラス。<br>
 * @see JobControlFinder
 * @since 3.6
 */
public class JobControlFinderImpl implements JobControlFinder {

    /**
     * ロガー。<br>
     */
    private static final TLogger LOGGER = TLogger.getLogger(
            AsyncJobOperatorImpl.class);

    protected SystemDao systemDao;

    /**
     * 先頭1行取得指定の条件.
     */
    private static final RowBounds LIMIT_ONE_ROWBOUNDS =
            new RowBounds(RowBounds.NO_ROW_OFFSET, 1);

    /**
     * コンストラクタ。
     */
    public JobControlFinderImpl(SystemDao systemDao) {
        Assert.notNull(systemDao, LOGGER.getLogMessage(LogId.EAL025069));
        this.systemDao = systemDao;
    }

    /**
     * ジョブ起動引数である{@code String}配列からジョブリスト取得用DAOの出力パラメータを取得する。<br>
     * 本メソッドではジョブの起動引数{@code args}はメソッド引数で渡されるが、ジョブリストの絞り込みには利用しない。
     * ジョブリストの絞り込みが必要となる場合は、別途本メソッドと呼び出し元、マッパーXMLファイルを拡張すること。
     *
     * @param args ジョブ起動引数
     * @return ジョブリスト取得用DAOの出力パラメータ
     */
    @Override
    public BatchJobListResult resolveBatchJobResult(String[] args) {
        BatchJobListParam param = new BatchJobListParam();
        param.setCurAppStatusList(new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add(JobStatusConstants.JOB_STATUS_UNEXECUTION);
            }
        });
        List<BatchJobListResult> resultList = systemDao.selectJobList(
                LIMIT_ONE_ROWBOUNDS, param);
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * ジョブ取得用DAOの入力パラメータからジョブパラメータを取得する。<br>
     * @param jobSequenceId ジョブのシーケンスID
     * @return ジョブパラメータ
     */
    @Override
    public BatchJobData resolveBatchJobData(String jobSequenceId) {
        BatchJobManagementParam batchJobManagementParam = new BatchJobManagementParam();
        batchJobManagementParam.setJobSequenceId(jobSequenceId);

        BatchJobData batchJobData = systemDao.selectJob(
                batchJobManagementParam);

        // 念のためトリムする
        if (batchJobData.getJobAppCd() != null) {
            batchJobData.setJobAppCd(batchJobData.getJobAppCd().trim());
        }

        return batchJobData;
    }

}