package org.odinallfather.odinsworld.util;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class OWLogger {

    private final String format = "[{TIME}] [{LOGGER_NAME}] {COLOR}({TYPE}) > {MESSAGE}";
    private final List<String> messagesToSave = Lists.newArrayList();
    private final String loggerName;

    public OWLogger(String loggerName) {
        this.loggerName = loggerName;
    }

    public void info(String message, Object... params) {
        String msg = this.getFormattedMessage(AnsiColor.GREEN, 0, message, params);
        messagesToSave.add(msg);
        System.out.println(msg + AnsiColor.RESET.getAnsiCode());
    }

    public void warning(String message, Object... params) {
        String msg = this.getFormattedMessage(AnsiColor.YELLOW, 1, message, params);
        messagesToSave.add(msg);
        System.out.println(msg + AnsiColor.RESET.getAnsiCode());
    }

    public void error(String message, Object... params) {
        String msg = this.getFormattedMessage(AnsiColor.RED, 2, message, params);
        messagesToSave.add(msg);
        System.out.println(msg + AnsiColor.RESET.getAnsiCode());
    }

    public void closeLogger() {
        File dir = new File("plugins//OdinsWorld//logs");
        if(!dir.exists()) {
            if(dir.mkdirs())
                System.out.println("Created logging directory");
            else
                System.out.println("Failed to create logging directory");
        }
        File file = new File(dir, getCurrentDate() + "_log.txt");
        try {
            if(!file.exists() && !file.createNewFile()) {
                System.out.println("Failed to create log file");
                return;
            }
            PrintWriter writer = new PrintWriter(file);
            writer.println("START LOGGING");
            this.messagesToSave.forEach(writer::println);
            writer.print("END LOGGING");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFormattedMessage(AnsiColor color, int type, String message, Object... params) {
        String msg = format;
        msg = msg.replace("{TIME}", getCurrentTime());
        msg = msg.replace("{LOGGER_NAME}", this.loggerName);
        msg = msg.replace("{COLOR}", color.getAnsiCode());
        switch (type) {
            case 0:
                msg = msg.replace("{TYPE}", "INFO");
                break;
            case 1:
                msg = msg.replace("{TYPE}", "WARNING");
                break;
            case 2:
                msg = msg.replace("{TYPE}", "ERROR");
                break;
            default:
                throw new IllegalArgumentException("Could not find type for logger");
        }
        msg = msg.replace("{MESSAGE}", String.format(message, params));
        return msg;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }


}
