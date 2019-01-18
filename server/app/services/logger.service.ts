import { mkdirSync, openSync, statSync, readdirSync, existsSync, renameSync, readFileSync, writeSync, closeSync } from "fs";
import { resolve } from "path";

export class Logger {
    private static readonly LOG_DIRECTORY: string = resolve(__dirname, "..", "..", "log");
    private static readonly LOG_FILE_PATH: string = resolve(Logger.LOG_DIRECTORY, "log.log");
    private static readonly MAX_LOG_SIZE: number = Math.pow(2, 16);
    private static readonly LOG_NAME_REGEX = /log-\d\.log/;

    public static warn(scope: string, message: string): void {
        Logger.writeLine(Logger.formatMessage("WARN", scope, message));
    }

    public static debug(scope: string, message: string): void {
        Logger.writeLine(Logger.formatMessage("DEBUG", scope, message));
    }

    public static info(scope: string, message: string): void {
        Logger.writeLine(Logger.formatMessage("INFO", scope, message));
    }

    public static error(scope: string, message: string): void {
        Logger.writeLine(Logger.formatMessage("ERROR", scope, message));
    }

    public static fatal(scope: string, message: string): void {
        Logger.writeLine(Logger.formatMessage("FATAL", scope, message));
    }

    private static checkFileSize(): void {
        if (!existsSync(Logger.LOG_FILE_PATH)) { return; }

        const fileSize: number = statSync(Logger.LOG_FILE_PATH).size;
        if (fileSize < Logger.MAX_LOG_SIZE) { return; }

        const files: Array<string> = readdirSync(Logger.LOG_DIRECTORY).filter(x => Logger.LOG_NAME_REGEX);
        for (let i: number = files.length - 1; i >= 0; --i) {
            renameSync(resolve(Logger.LOG_DIRECTORY, files[i]), resolve(Logger.LOG_DIRECTORY, Logger.generateFileName(i)));
        }
    }

    private static generateFileName(fileNumber: number): string {
        return "log-" + fileNumber + ".log";
    }

    private static formatMessage(level: string, scope: string, message: string): string {
        const date: string = (new Date()).toLocaleString("en-US");

        return date + "\t[" + level + "]\t[" + scope + "]\t" + message;
    }

    private static writeLine(message: string): void {
        Logger.checkFileSize();

        if (!existsSync(Logger.LOG_DIRECTORY)) {
            mkdirSync(Logger.LOG_DIRECTORY);
        }

        let previousData: Buffer = new Buffer("");
        if (existsSync(Logger.LOG_FILE_PATH)) {
            previousData = readFileSync(Logger.LOG_FILE_PATH);
        }

        const fd = openSync(Logger.LOG_FILE_PATH, "w+");

        const buffer: Buffer = new Buffer(message + "\n");
        writeSync(fd, buffer, 0, buffer.length, 0);
        writeSync(fd, previousData, 0, previousData.length, buffer.length);
        closeSync(fd);
    }
}