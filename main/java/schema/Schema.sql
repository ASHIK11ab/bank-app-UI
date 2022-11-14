CREATE TABLE bank (
    name VARCHAR(30) NOT NULL,
    website_url VARCHAR(30) NOT NULL,
    support_email VARCHAR(30) NOT NULL,
    support_phone BIGINT NOT NULL,
    --- account no set to null since account is created only after bank creation.
    account_no BIGINT NULL FOREIGN KEY (account_no) REFERENCES account(account_no)
);


CREATE TABLE banks (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL,
    contact_mail VARCHAR(30) UNIQUE NOT NULL,
    contact_phone BIGINT UNIQUE NOT NULL,
    api_url VARCHAR(70) NOT NULL
);


CREATE TABLE branch (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL,
    door_no VARCHAR(10) NOT NULL,
    street VARCHAR(30) NOT NULL,
    city VARCHAR(15) NOT NULL,
    state VARCHAR(15) NOT NULL,
    pincode INT NOT NULL
);


CREATE TABLE admin (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    password VARCHAR(15) NOT NULL,
    phone BIGINT UNIQUE NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL
);


ALTER SEQUENCE admin_id_seq RESTART 11111111;


CREATE TABLE manager (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    password VARCHAR(15) NOT NULL,
    phone BIGINT UNIQUE NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL,
    branch_id BIGINT NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES branch (id) ON DELETE CASCADE
);


ALTER SEQUENCE manager_id_seq RESTART 30001;


CREATE TABLE employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    password VARCHAR(15) NOT NULL,
    phone BIGINT UNIQUE NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL,
    branch_id SMALLINT NULL,
    FOREIGN KEY (branch_id) REFERENCES branch (id) ON DELETE CASCADE
);


ALTER SEQUENCE employee_id_seq RESTART 200001;


CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    phone BIGINT UNIQUE NOT NULL,
    email VARCHAR(30) UNIQUE NULL,
    password VARCHAR(15) NOT NULL,
    transaction_password VARCHAR(15) NOT NULL,
    removed_date DATE NULL
);


ALTER SEQUENCE customer_id_seq RESTART 10000001;


CREATE TABLE customer_info (
    customer_id BIGINT NOT NULL,
    age SMALLINT NOT NULL,
    gender CHAR(1) NOT NULL,
    martial_status VARCHAR(10) NOT NULL,
    occupation VARCHAR(20) NOT NULL,
    income INT NULL,
    adhaar BIGINT UNIQUE NOT NULL,
    pan VARCHAR(10) UNIQUE NULL,
    door_no VARCHAR(10) NOT NULL,
    street VARCHAR(30) NOT NULL,
    city VARCHAR(15) NOT NULL,
    state VARCHAR(15) NOT NULL,
    pincode INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE
);


-- Savings / Current / Salary, etch
CREATE TABLE regular_account_type (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(15) UNIQUE NOT NULL,
    rate_of_intrest REAL NULL,
    minimum_balance INT NOT NULL,
    daily_limit INT NULL,
    transactions_per_month SMALLINT NOT NULL -- No of free transactions allowed (ATM) 
);


-- FD / RD
CREATE TABLE deposit_account_type (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL,
    rate_of_intrest REAL NOT NULL
);


-- Personal / Home / etc
CREATE TABLE loan_account_type (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(15) UNIQUE NOT NULL,
    rate_of_intrest REAL NOT NULL,
    maximum_amount INT NOT NULL,         -- Maximum amount which can be granted in this loan type.
    repay_period SMALLINT NOT NULL       -- in months.
);


CREATE TABLE nominee (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name VARCHAR(15) NOT NULL,
    adhaar BIGINT NOT NULL,
    phone BIGINT NULL,
    relationship VARCHAR(15) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);


CREATE TABLE account (
    account_no BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    branch_id SMALLINT NOT NULL,
    nominee_id BIGINT NULL,
    balance REAL NOT NULL,
    opening_date DATE NOT NULL,
    closing_date DATE NULL,
    FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES branch (id),
    FOREIGN KEY (nominee_id) REFERENCES nominee (id)
);


ALTER SEQUENCE account_account_no_seq RESTART 10000000001;


CREATE TABLE regular_account (
    account_no BIGINT NOT NULL,
    type_id SMALLINT NOT NULL,
    active BOOLEAN NOT NULL,
    FOREIGN KEY (account_no) REFERENCES account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES regular_account_type (id)
);


CREATE TABLE deposit_account (
    account_no BIGINT NOT NULL,
    type_id SMALLINT NOT NULL,
    payout_account_no BIGINT NOT NULL,
    rate_of_intrest REAL NOT NULL,
    deposit_amount INT NULL,  -- For RD
    recurring_date DATE NULL,
    tenure_months INT NOT NULL,
    debit_from_account_no BIGINT NULL, -- optional auto debit from account.
    FOREIGN KEY (account_no) REFERENCES account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES deposit_account_type (id),
    FOREIGN KEY (payout_account_no) REFERENCES account (account_no),
    FOREIGN KEY (debit_from_account) REFERENCES account (account_no)
);


CREATE TABLE loan_account (
    account_no BIGINT NOT NULL,
    type_id SMALLINT NOT NULL,
    payout_account_no BIGINT NOT NULL,       -- account to which loan was credited.
    amount_granted INT NOT NULL,
    rate_of_intrest REAL NOT NULL,
    request_date DATE NOT NULL,
    approved_date DATE NOT NULL,
    emi_date DATE NOT NULL,
    tenure_months INT NOT NULL,
    amount_per_month INT NULL,      -- optional used with auto debit.
    debit_from_account BIGINT NULL, -- optional auto debit from account.
    FOREIGN KEY (account_no) REFERENCES account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES loan_account_type (id),
    FOREIGN KEY (payout_account_no) REFERENCES account(account_no),
    FOREIGN KEY (debit_from_account) REFERENCES account (account_no)
);


CREATE TABLE own_bank_beneficiary (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_no BIGINT NOT NULL,
    name VARCHAR(20) NOT NULL,
    nick_name VARCHAR(15) NOT NULL,
    FOREIGN KEY (account_no) references account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) references customer (id) ON DELETE CASCADE
);


CREATE TABLE other_bank_beneficiary (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    bank_id SMALLINT NOT NULL,
    account_no BIGINT NOT NULL,
    ifsc VARCHAR(11) NOT NULL,
    name VARCHAR(20) NOT NULL,
    nick_name VARCHAR(15) NOT NULL,
    FOREIGN KEY (bank_id) REFERENCES banks (id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) references customer (id) ON DELETE CASCADE
);


-- NEFT / ATM / UPI.
CREATE TABLE transaction_type (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(15) UNIQUE NOT NULL
);


CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    type_id SMALLINT NOT NULL,
    description VARCHAR NOT NULL,
    from_account_no BIGINT NULL,
    to_account_no BIGINT NULL,
    amount INT NOT NULL,
    date date not null,
    time time not null,
    FOREIGN KEY (type_id) REFERENCES transaction_type (id)
);


ALTER SEQUENCE transaction_id_seq RESTART 1000000000001;


CREATE TABLE account_transaction (
    account_no BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    before_balance INT NOT NULL,    -- balance in account before this transaction.
    FOREIGN KEY (account_no) REFERENCES account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transaction (id)
);


CREATE TABLE debit_card_type (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL,
    withdrawal_limit REAL NOT NULL
);


CREATE TABLE debit_card (
    card_no BIGSERIAL PRIMARY KEY,
    linked_account_no BIGINT NOT NULL,
    pin INT NOT NULL,
    valid_from DATE NOT NULL,
    expiry_date DATE NOT NULL,
    type_id SMALLINT NOT NULL,
    cvv INT NOT NULL,
    FOREIGN KEY (linked_account_no) REFERENCES account (account_no) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES debit_card_type (id)
);


ALTER SEQUENCE debit_card_card_no_seq RESTART 500000000001;