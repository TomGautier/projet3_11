/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

const TYPES =  {
        ServerInterface: Symbol.for("ServerInterface"),
        ApplicationInterface: Symbol.for("ApplicationInterface"),
        
        // Interfaces
        ConversationControllerInterface: Symbol.for("ConversationControllerInterface"),
        ConversationServiceInterface: Symbol.for("ConversationServiceInterface"),
        ConnectionControllerInterface:Symbol.for("ConnectionControllerInterface"),
        ConnectionServiceInterface:Symbol.for("ConnectionServiceInterface"),
        UserControllerInterface:Symbol.for("UserControllerInterface"),
        UserServiceInterface:Symbol.for("UserServiceInterface"),
        DrawingSessionServiceInterface:Symbol.for("DrawingSessionServiceInterface"),
        
        DateControllerInterface: Symbol.for("DateControllerInterface"),
        IndexControllerInterface: Symbol.for("IndexControllerInterface"),
        IndexServiceInterface: Symbol.for("IndexServiceInterface"),
        DateServiceInterface: Symbol.for("DateServiceInterface"),

        // Services 
        EventEmitter: Symbol.for("EventEmitter"),
        SocketService: Symbol.for("SocketService"),
        DatabaseConnection: Symbol.for("DatabaseConnection"),
        DatabaseService: Symbol.for("DatabaseService"),
        UserService:Symbol.for("UserService"),

        // Managers
        ConversationManager:Symbol.for("ConversationManager"),
        DrawingSessionManager:Symbol.for("DrawingSessionManager"),
        ConnectionManager:Symbol.for("ConnectionManager")
};

export  { TYPES };