import { Schema } from 'mongoose';
import { IUserModel } from '../models/user';
import mongoose = require('mongoose');

export let userSchema: Schema = new Schema({
    username: { 
        type: String, 
        index: true, 
        unique: true 
    },
    password: { 
        type: String
        /*UNTIL WE HAVE THE PASSWORD FEATURE ON THE FRONT END, 
        required: true*/ 
    }
});

export default mongoose.connection.model<IUserModel>('User', userSchema);