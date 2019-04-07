import { injectable, inject } from 'inversify';
import { Request, Response, Router, NextFunction } from 'express';

import { ConnectionService } from '../services/connection.service';
import { TYPES } from '.././types';

@injectable()
export class ConnectionController {

    public constructor( @inject(TYPES.ConnectionServiceInterface) private connectionService: ConnectionService) { }

    public get router(): Router {
        const router: Router = Router();

        router.post("/login/:username/:password",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                const valid = this.connectionService.onUserLogin(req.params.username, req.params.password)
                    .then(connectionId => res.send(connectionId));
            });
            
        router.post("/signup/:username/:password",
            (req: Request, res: Response, next: NextFunction) => {
                const valid = this.connectionService.onUserSignup(req.params.username, req.params.password)
                    .then(connectionId => res.send(connectionId))
            });


        router.post("/forgot/:username/:email",
            (req: Request, res: Response, next: NextFunction) => {
                const valid = this.connectionService.onForgotPassword(req.params.username, req.params.email)
                    .then(success => res.send(success))
            });
        
        router.post("/reset/:username/:password/:newPassword",
            (req: Request, res: Response, next: NextFunction) => {
                const valid = this.connectionService.onResetPassword(req.params.username, req.params.password, req.params.newPassword)
                    .then(success => res.send(success))
            });

        return router;
    }

}