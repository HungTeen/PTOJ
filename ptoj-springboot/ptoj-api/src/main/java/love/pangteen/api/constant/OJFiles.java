package love.pangteen.api.constant;

import java.io.File;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/23 9:41
 **/
public interface OJFiles {

    String USER_AVATAR_FOLDER = "/ptoj/file/avatar";

    String GROUP_AVATAR_FOLDER = "/ptoj/file/avatar/group";

    String HOME_CAROUSEL_FOLDER = "/ptoj/file/carousel";

    String MARKDOWN_FILE_FOLDER = "/ptoj/file/md";

    String PROBLEM_FILE_FOLDER = "/ptoj/file/problem";

    String CONTEST_TEXT_PRINT_FOLDER = "/ptoj/file/contest_print";

    String IMG_API = "/api/public/img/";

    String FILE_API = "/api/public/file/";

    String TESTCASE_TMP_FOLDER = "/ptoj/file/zip";

    String TESTCASE_BASE_FOLDER = "/ptoj/testcase";

    String FILE_DOWNLOAD_TMP_FOLDER = "/ptoj/file/zip/download";

    String CONTEST_AC_SUBMISSION_TMP_FOLDER = "/ptoj/file/zip/contest_ac";

    String RUN_WORKPLACE_DIR = "/judge/run";

    String TEST_CASE_DIR = "/judge/test_case";

    String SPJ_WORKPLACE_DIR = "/judge/spj";

    String INTERACTIVE_WORKPLACE_DIR = "/judge/interactive";

    String TMPFS_DIR = "/w";

    static String getTestCaseFolder(Long problemId){
        return OJFiles.TESTCASE_BASE_FOLDER + File.separator + "problem_" + problemId;
    }

    static String getJudgeCaseFolder(Long problemId){
        return OJFiles.TEST_CASE_DIR + File.separator + "problem_" + problemId;
    }
    
}
