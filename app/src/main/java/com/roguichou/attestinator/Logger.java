package com.roguichou.attestinator;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Logger {


    public static final int LOG_LEVEL_WARN = 0x0;
    public static final int LOG_LEVEL_INFO = 0x1;

    public static final String LOG_ERR = "E";
    public static final String LOG_WARN = "W";
    public static final String LOG_INFO = "I";

    private CSVWriter csvWriter = null;
    private int logLevel = LOG_LEVEL_WARN;
    private ArrayList<LogData> data;

    private String filename;

    public Logger(Context ctx, int _logLevel)
    {
        String fn = ctx.getFilesDir()+"/log.csv";
        try {
            File file = new File(fn);
            if (!file.exists())
            {
                file.createNewFile();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        init_writer(fn, _logLevel, StandardOpenOption.APPEND);
    }

    private void init_writer(String fn, int _logLevel, StandardOpenOption option)
    {
        logLevel = _logLevel;
        filename = fn;
        try
        {
            Writer writer = Files.newBufferedWriter(Paths.get(fn), option);
            csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        }
        catch (Exception e)
        {
            Log.d("Attestinator", e.toString());
        }

        data = new ArrayList<>();
    }

    public void log(String type, String log)
    {
        if (logLevel == LOG_LEVEL_WARN && type.equals(LOG_INFO))
        {
            return;
        }
        data.add(new LogData(type, Calendar.getInstance(), log));
        csvWriter.writeNext(new String[]{type, ""+Calendar.getInstance().getTimeInMillis() , log});
        csvWriter.flushQuietly();
    }

    public void endLog()
    {
        try
        {
            csvWriter.close();
        }
        catch (Exception e)
        {
            //tant pis
        }
    }


    public long getLogFileSize()
    {
        File file = new File(String.valueOf(Paths.get(filename)));
        return file.length();
    }

    public void clearLog()
    {
        endLog();
        init_writer(filename, logLevel, StandardOpenOption.WRITE);
        log(LOG_WARN, "Clear Log");
    }

    @Override
    @NonNull
    public String toString()
    {
        StringBuilder val = new StringBuilder();
        int sz = data.size();
        for(int i=sz;i>0;i--)
        {
            val.append(data.get(i-1).toString());
            if (i>1)
            {
                val.append("<br>");
            }
        }
        return val.toString();
    }

    static class LogData
    {
        private final String type;
        private final String log;
        private final Calendar cal;
        LogData(String _type, Calendar _cal, String _log)
        {
            type = _type;
            log = _log;
            cal=_cal;
        }

        @Override
        @NonNull
        public String toString()
        {
            StringBuilder val = new StringBuilder();
            switch (type)
            {
                case LOG_ERR:
                    val.append("<font color=\"red\">E");
                    break;
                case LOG_WARN :
                    val.append("<font color=\"#ff7f27\">W");
                    break;
                default:
                    val.append("<font color=\"grey\">I");
                    break;
            }
            SimpleDateFormat format = new SimpleDateFormat(" dd/MM/yy HH:mm ", Locale.FRENCH);
            val.append((format.format(cal.getTime())));

            val.append(log);

            val.append("</font>");
            return val.toString();
        }

    }

}
