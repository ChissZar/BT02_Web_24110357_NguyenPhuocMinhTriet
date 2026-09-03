IF DB_ID(N'jakartaJPA') IS NULL
BEGIN
    CREATE DATABASE jakartaJPA;
END
GO

USE jakartaJPA;
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        Id int IDENTITY(1,1) NOT NULL PRIMARY KEY,
        Email varchar(100) NULL,
        Username varchar(50) NOT NULL,
        FullName nvarchar(100) NULL,
        Password varchar(100) NOT NULL,
        Avatar nvarchar(500) NULL,
        RoleId int NOT NULL,
        Phone varchar(20) NULL,
        CreatedDate datetime2 NULL
    );
END
GO

IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        CategoryId int IDENTITY(1,1) NOT NULL PRIMARY KEY,
        CategoryName nvarchar(50) NOT NULL,
        Images nvarchar(500) NULL,
        Status int NOT NULL
    );
END
GO

IF OBJECT_ID(N'dbo.videos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.videos (
        VideoId varchar(50) NOT NULL PRIMARY KEY,
        Active int NOT NULL,
        Description nvarchar(500) NULL,
        Poster nvarchar(500) NULL,
        Title nvarchar(500) NULL,
        Views int NOT NULL,
        CategoryId int NULL,
        CONSTRAINT FK_videos_categories
            FOREIGN KEY (CategoryId) REFERENCES dbo.categories(CategoryId)
    );
END
GO
