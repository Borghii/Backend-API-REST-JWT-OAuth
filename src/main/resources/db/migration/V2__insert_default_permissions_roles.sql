-- Insert data for `permissions` table
INSERT INTO `permissions` (`permission_id`, `permission_name`) VALUES
                                                                   (1, 'CREATE'),
                                                                   (4, 'DELETE'),
                                                                   (2, 'READ'),
                                                                   (3, 'UPDATE');

-- Insert data for `roles` table
INSERT INTO `roles` (`role_id`, `role_name`) VALUES
                                                 (1, 'ADMIN'),
                                                 (2, 'DEVELOPER'),
                                                 (3, 'INVITED');

-- Insert data for `role_permission` table
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
                                                               (1, 1),
                                                               (1, 2),
                                                               (2, 2),
                                                               (3, 2),
                                                               (1, 3),
                                                               (2, 3),
                                                               (1, 4),
                                                               (2, 4);


