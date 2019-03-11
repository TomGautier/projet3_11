import { Schema } from 'mongoose';
import { IShapeModel } from '../models/shape';
import mongoose = require('mongoose');

export let shapeSchema: Schema = new Schema({
    id: {
        type: Schema.Types.ObjectId,
        required: true
    }, 
    drawingSessionId: {
        type: Schema.Types.ObjectId,
        required: true
    },
    author: {
        type: Schema.Types.ObjectId,
        ref: 'User'
    },
    properties: {
        type: { type: String, required: true },
        fillingColor: { type: String, required: true },
        borderColor: { type: String, required: true },
        middlePointCoord: { type: [Number], required: true },
        height: { type: Number, required: true },
        width: { type: Number, required: true },
        rotation: { type: Number }
    },

}, {timestamps: true});

export default mongoose.connection.model<IShapeModel>('Shape', shapeSchema);

