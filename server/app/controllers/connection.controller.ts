import { injectable, inject } from 'inversify';
import { Request, Response, Router, NextFunction } from 'express';

import { ConnectionService } from '../services/connection.service';
import { TYPES } from '.././types';

@injectable()
export class ConnectionController {

    public constructor( @inject(TYPES.ConnectionService) private connectionService: ConnectionService) { }

    public get router(): Router {
        const router: Router = Router();

        router.post("/login/:username/:password",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                // const conversations = await this.conversationService.getAll(); res.json(conversations);
                const valid = this.connectionService.onUserLogin(req.params.username, req.params.password);
                return valid ? 
                        res.send({success: true}) :
                        res.send({success: false});
            });
            
        return router;
    }
}