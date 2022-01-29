package fr.hyriode.hyggdrasil.util.logger;

import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.*;

public class HyggLogger extends Logger {

    private final HyggLogDispatcher dispatcher = new HyggLogDispatcher(this);

    private ConsoleReader consoleReader;

    @SuppressWarnings({"CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"})
    public HyggLogger(String name, String filePattern) {
        super(name, null);

        this.setLevel(Level.ALL);

        try {
            this.consoleReader = new ConsoleReader();
            this.consoleReader.setExpandEvents(false);

            final FileHandler fileHandler = new FileHandler(filePattern);
            fileHandler.setFormatter(new HyggdrasilConciseFormatter(this, false));
            this.addHandler(fileHandler);

            final HyggdrasilColouredWriter consoleHandler = new HyggdrasilColouredWriter(consoleReader);
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new HyggdrasilConciseFormatter(this, true));
            this.addHandler(consoleHandler);

        } catch (IOException e) {
            System.err.println("Couldn't register logger!");
            System.exit(-1);
        }

        System.setErr(new PrintStream(new HyggLoggingOutputStream(this, Level.SEVERE), true));
        System.setOut(new PrintStream(new HyggLoggingOutputStream(this, Level.INFO), true));

        this.dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        this.dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }

    public static void printHeaderMessage() {
        final String message =
                "$$\\   $$\\                                     $$\\                              $$\\ $$\\ \n" +
                "$$ |  $$ |                                    $$ |                             \\__|$$ |\n" +
                "$$ |  $$ |$$\\   $$\\  $$$$$$\\   $$$$$$\\   $$$$$$$ | $$$$$$\\  $$$$$$\\   $$$$$$$\\ $$\\ $$ |\n" +
                "$$$$$$$$ |$$ |  $$ |$$  __$$\\ $$  __$$\\ $$  __$$ |$$  __$$\\ \\____$$\\ $$  _____|$$ |$$ |\n" +
                "$$  __$$ |$$ |  $$ |$$ /  $$ |$$ /  $$ |$$ /  $$ |$$ |  \\__|$$$$$$$ |\\$$$$$$\\  $$ |$$ |\n" +
                "$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |     $$  __$$ | \\____$$\\ $$ |$$ |\n" +
                "$$ |  $$ |\\$$$$$$$ |\\$$$$$$$ |\\$$$$$$$ |\\$$$$$$$ |$$ |     \\$$$$$$$ |$$$$$$$  |$$ |$$ |\n" +
                "\\__|  \\__| \\____$$ | \\____$$ | \\____$$ | \\_______|\\__|      \\_______|\\_______/ \\__|\\__|\n" +
                "          $$\\   $$ |$$\\   $$ |$$\\   $$ |                                               \n" +
                "          \\$$$$$$  |\\$$$$$$  |\\$$$$$$  |                                               \n" +
                "           \\______/  \\______/  \\______/                                                ";

        System.out.println(message.replaceAll("\\$", "█"));
    }

    public ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    public HyggLogDispatcher getDispatcher() {
        return this.dispatcher;
    }

    private static class HyggdrasilConciseFormatter extends Formatter {

        private final Logger logger;
        private final boolean colored;

        public HyggdrasilConciseFormatter(Logger logger, boolean colored) {
            this.logger = logger;
            this.colored = colored;
        }

        @Override
        @SuppressWarnings("ThrowableResultIgnored")
        public String format(LogRecord record) {
            final StringBuilder formatted = new StringBuilder();

            formatted.append("[")
                    .append(this.logger.getName())
                    .append("] ")
                    .append("[");
            this.appendLevel(formatted, record.getLevel());
            formatted.append("] ")
                    .append(this.formatMessage(record))
                    .append("\n");

            if (record.getThrown() != null) {
                final StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                formatted.append(writer);
            }

            return formatted.toString();
        }

        private void appendLevel(StringBuilder builder, Level level) {
            if (this.colored) {
                HyggLogColor color;

                if (level == Level.INFO) {
                    color = HyggLogColor.DARK_AQUA;
                } else if (level == Level.WARNING) {
                    color = HyggLogColor.YELLOW;
                } else if (level == Level.SEVERE) {
                    color = HyggLogColor.RED;
                } else {
                    color = HyggLogColor.AQUA;
                }

                builder.append(color).append(level.getLocalizedName()).append(HyggLogColor.RESET);
            } else {
                builder.append(level.getLocalizedName());
            }
        }
    }

    private static class HyggdrasilColouredWriter extends Handler {

        private final Map<HyggLogColor, String> replacements = new EnumMap<>(HyggLogColor.class);
        private final HyggLogColor[] colors = HyggLogColor.values();
        private final ConsoleReader console;

        public HyggdrasilColouredWriter(ConsoleReader console) {
            this.console = console;

            this.replacements.put(HyggLogColor.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
            this.replacements.put(HyggLogColor.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
            this.replacements.put(HyggLogColor.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
            this.replacements.put(HyggLogColor.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
            this.replacements.put(HyggLogColor.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
            this.replacements.put(HyggLogColor.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
            this.replacements.put(HyggLogColor.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
            this.replacements.put(HyggLogColor.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
            this.replacements.put(HyggLogColor.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
            this.replacements.put(HyggLogColor.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
            this.replacements.put(HyggLogColor.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
            this.replacements.put(HyggLogColor.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
            this.replacements.put(HyggLogColor.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
            this.replacements.put(HyggLogColor.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
            this.replacements.put(HyggLogColor.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
            this.replacements.put(HyggLogColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
            this.replacements.put(HyggLogColor.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());
        }

        public void print(String s) {
            for (HyggLogColor color : this.colors) {
                s = s.replaceAll("(?i)" + color, this.replacements.get(color));
            }

            try {
                this.console.print(ConsoleReader.RESET_LINE + s + Ansi.ansi().reset());
                this.console.drawLine();
                this.console.flush();
            } catch (IOException ignored) {}
        }

        @Override
        public void publish(LogRecord record) {
            if (this.isLoggable(record)) {
                this.print(this.getFormatter().format(record));
            }
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }

}
