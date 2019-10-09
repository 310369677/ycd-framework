package com.ycd.common;

import com.ycd.common.util.SimpleUtil;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import tk.mybatis.spring.annotation.MapperScan;


@MapperScan(basePackages = {"com.ycd.common.repo.mybatis"})
@SpringBootApplication(scanBasePackages = "com.ycd")
public abstract class YcdApplication {

    protected static class YcdAppRunner extends SpringApplicationBuilder {

        private String version;

        public YcdAppRunner(Class<?> runClass, String version) {
            super(runClass);
            this.version = version;
            initBanner();
        }

        private void initBanner() {
            banner((envir, clazz, out) -> {
                String[] banners = bannerText();
                for (String line : banners) {
                    out.println(line);
                }
                int startLineSize=105;
                //添加版本号进去
                String paramVersion = (version != null) ? " (version:   " + version + ")" : "";
                StringBuilder padding = new StringBuilder();
                while (padding.length() < (startLineSize - (paramVersion.length()))/2+1) {
                    padding.append(" ");
                }
                out.println(AnsiOutput.toString(AnsiColor.DEFAULT,padding, AnsiColor.GREEN, paramVersion,
                        AnsiStyle.FAINT, padding));
                out.println();
            });
        }


        private String[] bannerText() {
            String[] result = {"                                           ,---,                             ,-.----.          ,-.----.   ",
                    "                                         ,---.'|                             \\    /  \\         \\    /  \\  ",
                    "                                         |   | :                             |   :    |        |   :    | ",
                    "      .--,            ,---.              |   | |           ,--.--.           |   | .\\ :        |   | .\\ : ",
                    "    /_ ./|           /     \\           ,--.__| |          /       \\          .   : |: |        .   : |: | ",
                    " , ' , ' :          /    / '          /   ,'   |         .--.  .-. |         |   |  \\ :        |   |  \\ : ",
                    "/___/ \\: |         .    ' /          .   '  /  |          \\__\\/: . .         |   : .  |        |   : .  | ",
                    " .  \\  ' |         '   ; :__         '   ; |:  |          ,\" .--.; |         :     |`-'        :     |`-' ",
                    "  \\  ;   :         '   | '.'|        |   | '/  '         /  /  ,.  |         :   : :           :   : :    ",
                    "   \\  \\  ;         |   :    :        |   :    :|        ;  :   .'   \\        |   | :           |   | :    ",
                    "    :  \\  \\         \\   \\  /          \\   \\  /          |  ,     .-./        `---'.|           `---'.|    ",
                    "     \\  ' ;          `----'            `----'            `--`---'              `---`             `---`    ",
                    "      `--`                                                                                                "};
            return result;
        }
    }


    public static YcdAppRunner of(Class<?> clazz, String version) {
        String paramVersion = "unknown";
        if (SimpleUtil.isNotEmpty(version)) {
            paramVersion = version;
        }
        return new YcdAppRunner(clazz, paramVersion);
    }

    public static void main(String[] args) {
        String val=" , ' , ' :          /    / '          /   ,'   |         .--.  .-. |         |   |  \\ :        |   |  \\ :";
        System.out.println(val.length());
    }
}
