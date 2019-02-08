/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

import { Container } from "inversify";
import { TYPES } from "./types";
import { Server } from "./server";
import { Application } from "./app";
import { IndexController } from "./controllers/index.controller";
import { DateController } from "./controllers/date.controller";
import { IndexService } from "./services/index.service";
import { DateService } from "./services/date.service";


import { DateServiceInterface,
         IndexServiceInterface,
         DateControllerInterface, 
         IndexControllerInterface, 
         ServerInterface,
         ApplicationInterface } from "./interfaces";
import { SocketService } from "./services/socket.service";
import { UnsaucedEventEmitter } from "./interfaces/events";

const container: Container = new Container();

container.bind<ServerInterface>(TYPES.ServerInterface).to(Server);
container.bind<ApplicationInterface>(TYPES.ApplicationInterface).to(Application);
container.bind<IndexControllerInterface>(TYPES.IndexControllerInterface).to(IndexController);
container.bind<IndexServiceInterface>(TYPES.IndexServiceInterface).to(IndexService);

container.bind<DateControllerInterface>(TYPES.DateControllerInterface).to(DateController);
container.bind<DateServiceInterface>(TYPES.DateServiceInterface).to(DateService);

container.bind<SocketService>(TYPES.SocketService).to(SocketService);
container.bind<UnsaucedEventEmitter>(TYPES.EventEmitter).to(UnsaucedEventEmitter);


export { container };
