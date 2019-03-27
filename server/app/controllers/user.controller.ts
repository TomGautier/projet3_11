import { injectable, inject } from 'inversify';
import { Router, Request, Response, NextFunction } from 'express';

import { TYPES } from '.././types';
import { UserService } from '../services/user.service';
import { UserManager } from '../services/user.manager';

@injectable()
export class UserController {

    public constructor( @inject(TYPES.UserServiceInterface) private userService: UserService,
                        @inject(TYPES.UserManager) private userManager: UserManager
    ) {}

    public get router(): Router {
        const router: Router = Router();

        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
               if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                  { res.json(403); return; }
                // Send the request to the service and send the response
                this.userService.getAll().then(users => {
                    res.json(users);
            });
        });

        return router;
        
    }   
}