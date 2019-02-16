/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

import * as express from "express";
import * as logger from "morgan";
import * as cookieParser from "cookie-parser";
import * as bodyParser from "body-parser";
import * as cors from "cors";
import { TYPES } from "./types";
import { injectable, inject } from "inversify";
import { IndexController } from "./controllers/index.controller";
import { DateController } from "./controllers/date.controller";
import { ApplicationInterface } from "./interfaces";
import { ConversationManager } from "./services/conversation.manager";
import { LoginService } from "./services/login.service";


@injectable()
export class Application implements ApplicationInterface {

    private readonly internalError: number = 500;
    public app: express.Application;

    public constructor(
            @inject(TYPES.IndexControllerInterface) private indexController: IndexController,
            @inject(TYPES.DateControllerInterface) private dateController: DateController,
            @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
            @inject(TYPES.LoginService) private loginService: LoginService) {
        this.app = express();
        this.config();
        this.bindRoutes();
    }

    private config(): void {
        // Middlewares configuration
        this.app.use(logger("dev"));
        this.app.use(bodyParser.json());
        this.app.use(bodyParser.urlencoded({ extended: true }));
        this.app.use(cookieParser());
        this.app.use(cors());
    }

    public bindRoutes(): void {
        this.app.use("/api/index", this.indexController.router);
        this.app.use("/api/date/", this.dateController.router);
        this.errorHandeling();
    }

    private errorHandeling(): void {
        this.app.use((req: express.Request, res: express.Response, next: express.NextFunction) => {
            const err: Error = new Error("Not Found");
            next(err);
        });

        // development error handler
        // will print stacktrace
        if (this.app.get("env") === "development") {
            // tslint:disable-next-line:no-any
            this.app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
                res.status(err.status || this.internalError);
                res.send({
                    message: err.message,
                    error: err
                });
            });
        }

        // production error handler
        // no stacktraces leaked to user (in production env only)
        // tslint:disable-next-line:no-any
        this.app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
            res.status(err.status || this.internalError);
            res.send({
                message: err.message,
                error: {}
            });
        });
    }
}
