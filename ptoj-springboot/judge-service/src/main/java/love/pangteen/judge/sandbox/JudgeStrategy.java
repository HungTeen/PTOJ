package love.pangteen.judge.sandbox;

import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.exception.CompileError;
import love.pangteen.judge.pojo.entity.LanguageConfig;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/31 17:18
 **/
public class JudgeStrategy {

    /**
     * 检查是否为spj或者interactive，同时是否有对应编译完成的文件，若不存在，就先编译生成该文件，同时也要检查版本。
     */
    public static boolean checkOrCompileExtraProgram(Problem problem) throws CompileError, JudgeSystemError {

        JudgeMode judgeMode = JudgeMode.getJudgeMode(problem.getJudgeMode());

        String currentVersion = problem.getCaseVersion();

        LanguageConfig languageConfig;

        String programFilePath;

        String programVersionPath;

        switch (judgeMode) {
            case DEFAULT:
                return true;
            case SPJ:
//                languageConfig = languageConfigLoader.getLanguageConfigByName("SPJ-" + problem.getSpjLanguage());
//
//                programFilePath = Constants.JudgeDir.SPJ_WORKPLACE_DIR.getContent() + File.separator +
//                        problem.getId() + File.separator + languageConfig.getExeName();
//
//                programVersionPath = Constants.JudgeDir.SPJ_WORKPLACE_DIR.getContent() + File.separator +
//                        problem.getId() + File.separator + "version";
//
//                // 如果不存在该已经编译好的程序，则需要再次进行编译
//                if (!FileUtil.exist(programFilePath) || !FileUtil.exist(programVersionPath)) {
//                    boolean isCompileSpjOk = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
//                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
//
//                    FileWriter fileWriter = new FileWriter(programVersionPath);
//                    fileWriter.write(currentVersion);
//                    return isCompileSpjOk;
//                }
//
//                FileReader spjVersionReader = new FileReader(programVersionPath);
//                String recordSpjVersion = spjVersionReader.readString();
//
//                // 版本变动也需要重新编译
//                if (!currentVersion.equals(recordSpjVersion)) {
//                    boolean isCompileSpjOk = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
//                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
//                    FileWriter fileWriter = new FileWriter(programVersionPath);
//                    fileWriter.write(currentVersion);
//                    return isCompileSpjOk;
//                }

                break;
            case INTERACTIVE:
//                languageConfig = languageConfigLoader.getLanguageConfigByName("INTERACTIVE-" + problem.getSpjLanguage());
//                programFilePath = Constants.JudgeDir.INTERACTIVE_WORKPLACE_DIR.getContent() + File.separator +
//                        problem.getId() + File.separator + languageConfig.getExeName();
//
//                programVersionPath = Constants.JudgeDir.INTERACTIVE_WORKPLACE_DIR.getContent() + File.separator +
//                        problem.getId() + File.separator + "version";
//
//                // 如果不存在该已经编译好的程序，则需要再次进行编译 版本变动也需要重新编译
//                if (!FileUtil.exist(programFilePath) || !FileUtil.exist(programVersionPath)) {
//                    boolean isCompileInteractive = Compiler.compileInteractive(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
//                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
//                    FileWriter fileWriter = new FileWriter(programVersionPath);
//                    fileWriter.write(currentVersion);
//                    return isCompileInteractive;
//                }
//
//                FileReader interactiveVersionFileReader = new FileReader(programVersionPath);
//                String recordInteractiveVersion = interactiveVersionFileReader.readString();
//
//                // 版本变动也需要重新编译
//                if (!currentVersion.equals(recordInteractiveVersion)) {
//                    boolean isCompileInteractive = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
//                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
//
//                    FileWriter fileWriter = new FileWriter(programVersionPath);
//                    fileWriter.write(currentVersion);
//
//                    return isCompileInteractive;
//                }

                break;
            default:
                throw new RuntimeException("The problem mode is error:" + judgeMode);
        }

        return true;
    }
}
