import { Schema } from 'mongoose';
import { IShapeConnectionModel } from '../models/shapeConnection';
import mongoose = require('mongoose');

export let shapeConnectionSchema: Schema = new Schema({
    id: {
        type: String,
        required: true
    }, 
    imageId: {
        type: String,
        required: true
    },
    author: {
        type: String,
        ref: 'User'
    },
    properties: {
        type: String,
        color: String, 
        points: [Number], 
        shape1id: String, 
        shape2id: String 
    },

}, {timestamps: true});

export default mongoose.connection.model<IShapeConnectionModel>('ShapeConnection', shapeConnectionSchema);

