/*
 * Copyright (c) 2016 NTT DATA Corporation
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

package jp.terasoluna.fw.batch.executor;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import jp.terasoluna.fw.batch.blogic.BLogic;
import jp.terasoluna.fw.batch.blogic.vo.BLogicParam;
import jp.terasoluna.fw.batch.exception.handler.ExceptionHandler;
import jp.terasoluna.fw.batch.executor.vo.BLogicResult;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * {@code BLogicExecutorImpl}のテストケース
 * @since 3.6
 */
public class BLogicExecutorImplTest {

    private TestLogger logger = TestLoggerFactory.getTestLogger(
            BLogicExecutorImpl.class);

    private BLogicExecutorImpl target = new BLogicExecutorImpl();

    private ApplicationContext applicationContext = mock(
            ApplicationContext.class);

    private BLogic blogic = mock(BLogic.class);

    private BLogicParam blogicParam = new BLogicParam();

    private ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

    @SuppressWarnings("deprecation")
    private Map<?, ?> tga = (Map<?, ?>) ReflectionTestUtils.getField(
            ThreadGroupApplicationContextHolder.class, "tga");

    /**
     * テスト前処理
     */
    @Before
    public void setUp() {
        tga.clear();
        reset(applicationContext, blogic, exceptionHandler);
    }

    /**
     * テスト後処理
     */
    @After
    public void tearDown() {
        tga.clear();
        logger.clear();
    }

    /**
     * コンストラクタのテスト 【正常系】
     * 
     * <pre>
     * 事前条件
     * ・特になし
     * 確認項目
     * ・例外が発生しないこと
     * </pre>
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testBLogicExecutorImpl01() throws Exception {
        // テスト実施
        new BLogicExecutorImpl();
    }

    /**
     * execute()のテスト 【正常系】
     * 
     * <pre>
     * 事前条件
     * ・引数に適切なオブジェクトを与えること
     * 確認項目
     * ・正常にBLogicが実行された場合、その結果ステータスが返却されること
     * </pre>
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testExecute01() throws Exception {
        // 事前準備
        when(blogic.execute(blogicParam)).thenReturn(0);
        // テスト実施
        BLogicResult result = target.execute(applicationContext, blogic,
                blogicParam, exceptionHandler);
        // 結果検証
        assertEquals(0, result.getBlogicStatus());
        assertNull(result.getBlogicThrowable());
        assertEquals(0, tga.size());
        assertThat(logger.getLoggingEvents(), is(asList(debug(
                "[DAL025044] The BLogic detail is as follow. BLogic-class:"
                        + blogic.getClass().getName() + ", " + blogicParam
                                .toString()))));

    }

    /**
     * execute()のテスト 【異常系】
     * 
     * <pre>
     * 事前条件
     * ・引数に適切なオブジェクトを与えること
     * ・BLogicで例外が発生すること
     * 確認項目
     * ・例外ハンドラが起動した結果を反映した返却値を取得できること
     * </pre>
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testExecute02() throws Exception {
        // 事前準備
        RuntimeException re = new RuntimeException("test");
        when(blogic.execute(blogicParam)).thenThrow(re);
        when(exceptionHandler.handleThrowableException(re)).thenReturn(100);
        // テスト実施
        BLogicResult result = target.execute(applicationContext, blogic,
                blogicParam, exceptionHandler);
        // 結果検証
        assertEquals(100, result.getBlogicStatus());
        assertEquals(re, result.getBlogicThrowable());
        assertEquals(0, tga.size());

        assertThat(logger.getLoggingEvents(), is(asList(debug(
                "[DAL025044] The BLogic detail is as follow. BLogic-class:"
                        + blogic.getClass().getName() + ", " + blogicParam
                                .toString()))));
    }

    /**
     * execute()のテスト 【異常系】
     * 
     * <pre>
     * 事前条件
     * ・例外ハンドラにNULLを指定し、それ以外は適切な引数を指定すること
     * ・BLogicで例外が発生すること
     * 確認項目
     * ・例外ハンドラ起動せず、発生した例外とデフォルトの終了値になること
     * </pre>
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testExecute03() throws Exception {
        // 事前準備
        RuntimeException re = new RuntimeException("test");
        when(blogic.execute(blogicParam)).thenThrow(re);
        // テスト実施
        BLogicResult result = target.execute(applicationContext, blogic,
                blogicParam, null);
        // 結果検証
        assertEquals(255, result.getBlogicStatus());
        assertEquals(re, result.getBlogicThrowable());
        assertEquals(0, tga.size());

        assertThat(logger.getLoggingEvents(), is(asList(
                debug("[DAL025044] The BLogic detail is as follow. BLogic-class:" + blogic.getClass().getName() + ", " + blogicParam.toString()),
                error(re, "[EAL025057] An exception occurred at BLogic execution. This error log should be logged by the exception-handler, but the handler is not set."))));
    }

    /**
     * execute()のテスト 【異常系】
     * 
     * <pre>
     * 事前条件
     * ・例外ハンドラにNULLを指定し、それ以外は適切な引数を指定すること
     * ・BLogicでErrorが発生すること
     * 確認項目
     * ・例外ハンドラ起動せず、発生したErrorとデフォルトの終了値になること
     * </pre>
     *
     * @throws Exception 予期しない例外
     */
    @Test
    public void testExecute04() throws Exception {
        // 事前準備
        Error er = new Error("test");
        when(blogic.execute(blogicParam)).thenThrow(er);
        // テスト実施
        BLogicResult result = target.execute(applicationContext, blogic,
                blogicParam, null);
        // 結果検証
        assertEquals(255, result.getBlogicStatus());
        assertEquals(er, result.getBlogicThrowable());
        assertEquals(0, tga.size());

        assertThat(logger.getLoggingEvents(), is(asList(
                debug("[DAL025044] The BLogic detail is as follow. BLogic-class:" + blogic.getClass().getName() + ", " + blogicParam.toString()),
                error(er, "[EAL025057] An exception occurred at BLogic execution. This error log should be logged by the exception-handler, but the handler is not set."))));
    }

}
