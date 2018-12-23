INSERT INTO Topic (name) VALUES ('Security');
INSERT INTO Difficulty_Level (level, reward, topic_id) VALUES (1, 100, 1);
INSERT INTO Question (text) VALUES ('What does the fox say?');
INSERT INTO Difficulty_Level_Questions (difficulty_level_id, questions_id) VALUES (1, 1);
INSERT INTO Answer_Option (text, correct) VALUES ('dindindin', TRUE);
INSERT INTO Answer_Option (text, correct) VALUES ('baam baam baam', FALSE);
INSERT INTO Question_Answer_Options (question_id, answer_options_id) VALUES (1, 1);
INSERT INTO Question_Answer_Options (question_id, answer_options_id) VALUES (1, 2);
