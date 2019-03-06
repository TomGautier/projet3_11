import { assert } from 'chai';

import { DatabaseService, DatabaseConnection } from './database.service';

//import { itAsync } from '../utils/jasmine.extensions';
import { IMock, Mock } from 'typemoq';

describe('Database Service', () => {
    let databaseConnectionMock: IMock<DatabaseConnection>;
    let databaseService: DatabaseService;
    let model: any;

    beforeEach(async () => {
        databaseConnectionMock = Mock.ofType<DatabaseConnection>();
        databaseService = new DatabaseService(databaseConnectionMock.object);
        await databaseService.init();
    });

    it('create() - Should return the document parsed from the response body.', async () => {
        // Arrange
        const expectedDoc = { content: 'test' };

        model = {
            create: (dict: any, callback: any) => Promise.resolve(callback(undefined, expectedDoc))
        };

        let actualDoc;

        // Act
        actualDoc = await databaseService.create(model, expectedDoc).catch(err => console.log(err));

        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('create() - Should return an error.', async () => {
        // Arrange
        const expectedErr = 'Erreur';

        model = {
            create: (dict: any, callback: any) => Promise.reject(callback(expectedErr, undefined))
        };

        let actualErr;

        // Act
        await databaseService.create(model, undefined).catch(err => actualErr = err);

        // Assert
        assert.equal(JSON.stringify(actualErr), JSON.stringify(expectedErr));
    });

    it('getByCriteria() - Should return the document parsed from the response body.', async () => {
        // Arrange
        const expectedDoc = { content: 'test' };

        model = {
            findOne: (dict: any, callback: any) => Promise.resolve(callback(undefined, expectedDoc))
        };

        let actualDoc;
        const criteria = 'criteria';
         // Act 
        actualDoc = await databaseService.getByCriteria(model, criteria, expectedDoc).catch(err => console.log(err));
         
        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('getByCriteria() - Should return an error.', async () => {
        // Arrange
        const expectedErr = 'Erreur';

        model = {
            findOne: (dict: any, callback: any) => Promise.reject(callback(expectedErr, undefined))
        };

        let actualErr;
        const criteria = 'criteria';
         // Act
        await databaseService.getByCriteria(model, criteria, undefined).catch(err => actualErr = err);

        // Assert
        assert.equal(JSON.stringify(actualErr), JSON.stringify(expectedErr));
    });

    it('getAll() - Should return the document parsed from the response body.', async () => {
        // Arrange
        const expectedDoc = { content: 'test' };

        model = {
            find: (dict: any, callback: any) => Promise.resolve(callback(undefined, expectedDoc))
        };

        let actualDoc;

        // Act
        actualDoc = await databaseService.getAll(model).catch(err => console.log(err));
        
        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('getAll() - Should return an error.', async () => {
        // Arrange
        const expectedErr = 'Erreur';

        model = {
            find: (dict: any, callback: any) => Promise.reject(callback(expectedErr, undefined))
        };

        let actualErr;

        // Act
        await databaseService.getAll(model).catch(err => actualErr = err);

        // Assert
        assert.equal(JSON.stringify(actualErr), JSON.stringify(expectedErr));
    });

    it('getAllByCriteria() - Should return the document parsed from the response body.', async () => {
        // Arrange
        const expectedDoc = { content: 'test' };

        model = {
            find: (dict: any, callback: any) => Promise.resolve(callback(undefined, expectedDoc))
        };

        let actualDoc;
        const criteria = 'criteria';
        // Act
        actualDoc = await databaseService.getAllByCriteria(model, criteria, undefined).catch(err => console.log(err));
        
        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('getAllByCriteria() - Should return an error.', async () => {
        // Arrange
        const expectedErr = 'Erreur';

        model = {
            find: (dict: any, callback: any) => Promise.reject(callback(expectedErr, undefined))
        };

        let actualErr;
        const criteria = 'criteria';
        // Act
        await databaseService.getAllByCriteria(model, criteria, undefined).catch(err => actualErr = err);

        // Assert
        assert.equal(JSON.stringify(actualErr), JSON.stringify(expectedErr));
    });

    it('delete() - Should return the document parsed from the response body.', async () => {
        // Arrange
        const expectedDoc = { content: 'test' };

        model = {
            remove: (dict: any, callback: any) => Promise.resolve(callback(undefined, expectedDoc))
        };

        let actualDoc;

        // Act
        actualDoc = await databaseService.remove(model, 'criteria', expectedDoc).catch(err => console.log(err));
        
        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('delete() - Should return an error.', async () => {
        // Arrange
        const expectedErr = 'Erreur';

        model = {
            remove: (dict: any, callback: any) => Promise.reject(callback(expectedErr, undefined))
        };

        let actualErr;

        // Act
        await databaseService.remove(model, 'criteria', undefined).catch(err => actualErr = err);

        // Assert
        assert.equal(JSON.stringify(actualErr), JSON.stringify(expectedErr));
    });
});