import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { MessageService } from "../services/message.service";
import { MessageControllerInterface } from "../interfaces";

@injectable()
export class MessageController implements MessageControllerInterface {
    
    public constructor(
        @inject(TYPES.MessageServiceInterface) private messageService: MessageService
    ) { }

    public get router(): Router {
        const router: Router = Router();
        
        router.get("/:conversationId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                this.messageService.getAllFromConversation(req.params.conversationName).then(messages => {
                    res.json(messages);
                });
            });

        router.post("/:conversationId",
            (req: Request, res: Response, next: NextFunction) => {
                this.messageService.sendMessage({
                    name: req.params.conversationName,
                    message: req.body,
                    author: 'baba'}
                    ).then(message => {
                    res.json(message);
                });
            });
            
        return router;
    }
}