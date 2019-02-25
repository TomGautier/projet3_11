import { Schema } from 'mongoose';
import { IShapeModel } from '../models/Shape';
import mongoose = require('mongoose');

export let shapeSchema: Schema = new Schema({
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

export default mongoose.connection.model<IShapeModel>('Shape', shapeSchema);

