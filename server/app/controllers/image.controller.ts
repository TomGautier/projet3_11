import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { UserManager } from "../services/user.manager";
import { ImageControllerInterface } from "../interfaces";
import { ImageService } from "../services/image.service";
import { DrawingSessionManager } from "../services/drawingSession.manager";

@injectable()
export class ImageController implements ImageControllerInterface {
    
    public constructor(
        @inject(TYPES.ImageServiceInterface) private imageService: ImageService,
        @inject(TYPES.UserManager) private userManager: UserManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        router.get("/single/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getById(req.params.imageId).then(image => {
                    res.json(image);
                })
                .catch(err => {
                    res.json(400);
                });
            });

        router.get("/:sessionId/:username/:visibility",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                switch(req.params.visibility) {
                    case 'public': 
                        this.imageService.getAll().then(images => {
                            res.json(images);
                        })
                        .catch(err => {
                            res.json(400);
                        });
                        break;
                    case 'private':
                        this.imageService.getAllByAuthor(req.params.username).then(images => {
                            res.json(images);
                        })
                        .catch(err => {
                            res.json(400);
                        });
                        break;
                    default:
                        break;
                }
            });

        router.post("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                //if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                  //  { res.json(403); return; }
                console.log("body", req.body);
                this.imageService.create(req.body.imageId, 
                                        req.body.author,
                                        req.body.visibility,
                                        req.body.protection)
                    .then(image => {
                        res.json(image);
                    })
                    .catch(err => {
                        res.json(400);
                    });
            });

        router.post("/protection/:sessionId/:username/:imageId/:newProtection",
            (req: Request, res: Response, next: NextFunction) => {
                //if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                  //  { res.json(403); return; }
                this.imageService.updateProtection(req.params.imageId, req.params.protection)
                    .then(success => {
                        res.json(success);
                    })
                    .catch(err => {
                        res.json(400);
                    });
            });

        router.post("/thumbnail/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json(403); return; }
                
                this.imageService.updateThumbnail(req.params.imageId, req.body.thumbnail, req.body.thumbnailTimestamp)
                    .then(image => {
                        res.json(image);
                    })
                    .catch(err => {
                        res.json(400);
                    });

            });

        return router;
    }
}