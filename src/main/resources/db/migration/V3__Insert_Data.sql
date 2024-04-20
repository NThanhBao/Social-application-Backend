-- Chèn dữ liệu vào bảng users
INSERT INTO users (id, username, password, first_name, last_name, role, gender, phone_number, date_of_birth, mail, avatar,address, enable)
VALUES
    (UUID(), 'admin1', 'admin123', 'John', 'Doe', 'admin', TRUE, '123456789', '1990-01-01', 'admin1@example.com', "","Bình Định", TRUE),
    (UUID(), 'admin2', 'admin123', 'Jane', 'Doe', 'admin', FALSE, '987654321', '1992-03-15', 'admin2@example.com', "","Bình Định", TRUE);

