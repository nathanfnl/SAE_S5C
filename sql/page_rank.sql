-- Create page_ranks table
CREATE TABLE dblp.page_ranks (
    author_id integer PRIMARY KEY,
    page_rank double precision
);

-- Create page_ranks_weight table
CREATE TABLE dblp.page_ranks_weight (
    author_id integer PRIMARY KEY,
    page_rank double precision
);

-- Create page_ranks_aff table
CREATE TABLE dblp.page_ranks_aff (
    author_id integer,
    page_rank double precision,
    affiliation_id integer,
    PRIMARY KEY (author_id, affiliation_id)
);

-- Create page_ranks_aff_weight table
CREATE TABLE dblp.page_ranks_aff_weight (
    author_id integer,
    page_rank double precision,
    affiliation_id integer,
    PRIMARY KEY (author_id, affiliation_id)
);

