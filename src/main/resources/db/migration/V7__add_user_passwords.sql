-- Users log in with email + password. Only a BCrypt hash of the password is stored, never the password itself.
ALTER TABLE users ADD COLUMN password_hash VARCHAR(100);

-- Sample users (local development only): the password for all three is "campus123"
UPDATE users SET password_hash = '{bcrypt}$2a$10$bnRkydUdF6L0F2PEQltd6u86AGPj8zmio5mHa1tbWKCWse1WY3S.S';

ALTER TABLE users ALTER COLUMN password_hash SET NOT NULL;
