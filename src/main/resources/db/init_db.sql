DROP TABLE IF EXISTS gift;

CREATE TABLE IF NOT EXISTS gift (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    importance VARCHAR(50) NOT NULL
)