import { Schema } from 'mongoose';
import { IMessageModel } from '../models/message';
import mongoose = require('mongoose');

export let messageSchema: Schema = new Schema({
    conversationId: {
        type: Schema.Types.ObjectId,
        required: true
    },
    body: {
        type: String,
        required: true
    },
    author: {
        type: Schema.Types.ObjectId,
        ref: 'User'
    }
});

export default mongoose.connection.model<IMessageModel>('Message', messageSchema);

