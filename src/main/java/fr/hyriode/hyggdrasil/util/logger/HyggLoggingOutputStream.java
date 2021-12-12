package fr.hyriode.hyggdrasil.util.logger;

import com.google.common.base.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HyggLoggingOutputStream extends ByteArrayOutputStream {

    private static final String separator = System.getProperty("line.separator");

    private final Logger logger;
    private final Level level;

    public HyggLoggingOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException
    {
        final String contents = this.toString(Charsets.UTF_8.name());
        super.reset();
        if (!contents.isEmpty() && !contents.equals(separator)) {
            this.logger.logp(level, "", "", contents);
        }
    }

}
