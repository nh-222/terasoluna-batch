package jp.terasoluna.fw.file.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * {@link jp.terasoluna.fw.file.annotation.StringConverterToUpperCase} クラスのテスト。
 * <p>
 * <h4>【クラスの概要】</h4> アノテーションStringConverterの記述に従い、文字列の変換処理を行う。<br>
 * StringConverterToUpperCaseは入力された文字列（アルファベット） 文字列を全て大文字にして返却する。
 * <p>
 * @see jp.terasoluna.fw.file.annotation.StringConverterToUpperCase
 */
public class StringConverterToUpperCaseTest {

    /**
     * testConvert01() <br>
     * <br>
     * (正常系) <br>
     * 観点：C <br>
     * <br>
     * 入力値：(引数) string:null<br>
     * <br>
     * 期待値：(戻り値) resultString:null<br>
     * <br>
     * 引数にNullが入った場合の処理。<br>
     * nullを返却する。 <br>
     * @throws Exception このメソッドで発生した例外
     */
    @Test
    public void testConvert01() throws Exception {
        // 前処理(引数)
        StringConverter stringTransformer = new StringConverterToUpperCase();

        // テスト実施
        String resultString = stringTransformer.convert(null);

        // 判定
        assertNull(resultString);
    }

    /**
     * testConvert02() <br>
     * <br>
     * (正常系) <br>
     * 観点：E <br>
     * <br>
     * 入力値：(引数) string:not null<br>
     * 以下のStringインスタンス<br>
     * "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ"<br>
     * <br>
     * 期待値：(戻り値) resultString:以下のStringインスタンス<br>
     * "ABCDEFGHIJKLMNOPQRSTUVWXYZ_ABCDEFGHIJKLMNOPQRSTUVWXYZ"<br>
     * <br>
     * 文字変換処理を実施。<br>
     * この場合、大文字変換処理なので、入力した文字列を全て大文字に変換して返却する。 <br>
     * @throws Exception このメソッドで発生した例外
     */
    @Test
    public void testConvert02() throws Exception {
        // 前処理(引数)
        StringConverter stringTransformer = new StringConverterToUpperCase();

        // テスト実施
        String resultString = stringTransformer
                .convert("abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        // 判定
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ_ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                resultString);
    }

    /**
     * testConvert03() <br>
     * <br>
     * (正常系) <br>
     * 観点：E <br>
     * <br>
     * 入力値：(引数) string:not null<br>
     * 以下のStringインスタンス<br>
     * "あいうカキク漢字"<br>
     * <br>
     * 期待値：(戻り値) resultString:以下のStringインスタンス<br>
     * "あいうカキク漢字"<br>
     * <br>
     * 文字変換処理を実施。<br>
     * ひらがな、カタカナ、漢字がある場合は、そのまま出力されることを確認する。 <br>
     * @throws Exception このメソッドで発生した例外
     */
    @Test
    public void testConvert03() throws Exception {
        // 前処理(引数)
        StringConverter stringTransformer = new StringConverterToLowerCase();

        // テスト実施
        String resultString = stringTransformer.convert("あいうカキク漢字");
        // 判定
        assertEquals("あいうカキク漢字", resultString);
    }

    /**
     * testConvert04() <br>
     * <br>
     * (正常系) <br>
     * 観点：E <br>
     * <br>
     * 入力値：(引数) string:以下のStringインスタンス<br>
     * " "(空文字）<br>
     * <br>
     * 期待値：(戻り値) resultString:以下のStringインスタンス<br>
     * " "(空文字）<br>
     * <br>
     * 文字変換処理を実施。<br>
     * ""(空文字）の場合は、そのまま変換されずに出力されることを確認する。 <br>
     * @throws Exception このメソッドで発生した例外
     */
    @Test
    public void testConvert04() throws Exception {
        // 前処理(引数)
        StringConverter stringTransformer = new StringConverterToLowerCase();

        // テスト実施
        String resultString = stringTransformer.convert("");

        // 判定
        assertEquals("", resultString);
    }

}
