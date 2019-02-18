import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { ConversationService } from "../services/conversation.service";
import { ConversationControllerInterface } from "../interfaces";

@injectable()
export class ConversationController implements ConversationControllerInterface {
    
    public constructor(
        @inject(TYPES.ConversationServiceInterface) private conversationService: ConversationService
    ) { }

    public get router(): Router {
        const router: Router = Router();
        
        router.get("/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                // const conversations = await this.conversationService.getAll(); res.json(conversations);
                this.conversationService.getAllByUsername(req.params.username).then(conversations => {
                    res.json(conversations);
                });
            });

        router.post("/:username/:conversationName",
            (req: Request, res: Response, next: NextFunction) => {
                this.conversationService.create(req.params.conversationName, req.params.username).then(conversation => {
                    res.json(conversation);
                });
            });
            
        return router;
    }
}