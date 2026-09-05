USE jakartaJPA;
GO

IF OBJECT_ID('dbo.account_security', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.account_security (
        userId INT NOT NULL PRIMARY KEY,
        activated BIT NOT NULL,
        otpHash VARCHAR(100) NULL,
        purpose VARCHAR(12) NULL,
        expiresAt DATETIME2 NULL,
        sentAt DATETIME2 NULL,
        attempts INT NOT NULL,
        CONSTRAINT FK_account_security_users FOREIGN KEY (userId) REFERENCES dbo.users(Id)
    );
END;
GO

IF OBJECT_ID('dbo.registration_keys', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.registration_keys (
        identityKey VARCHAR(110) NOT NULL PRIMARY KEY
    );
END;
GO
