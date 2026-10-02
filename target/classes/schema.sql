-- FoodRescue database schema (PostgreSQL) - v2
-- Adds: 3 user types (Provider/NGO/Volunteer), verification, profile fields,
--       claim-request + accept/reject workflow, ratings, distribution plan.
-- Runs automatically every time the app starts. Safe to run again and again.
-- NOTE: this is a NEW schema shape. If you have the old database, DROP it and
-- let the app recreate everything fresh (see the setup notes you were given).

-- 1) users : everyone who can log in (provider, NGO, or volunteer)
CREATE TABLE IF NOT EXISTS users (
    id                 BIGSERIAL PRIMARY KEY,
    full_name          VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL UNIQUE,
    password_hash      VARCHAR(100) NOT NULL,
    phone              VARCHAR(20),
    role               VARCHAR(20)  NOT NULL CHECK (role IN ('PROVIDER', 'NGO', 'VOLUNTEER')),
    is_admin           BOOLEAN      NOT NULL DEFAULT FALSE,
    profile_photo_path VARCHAR(255),
    bio                VARCHAR(500),
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2) food_providers : extra details for users with role PROVIDER
CREATE TABLE IF NOT EXISTS food_providers (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT       NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    business_name VARCHAR(150) NOT NULL,
    business_type VARCHAR(50),
    address       VARCHAR(255) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    latitude      DECIMAL(9,6),
    longitude     DECIMAL(9,6)
);

-- 3) organizations : extra details for users with role NGO (needs verification)
CREATE TABLE IF NOT EXISTS organizations (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    org_name            VARCHAR(150) NOT NULL,
    registration_no     VARCHAR(50),
    address             VARCHAR(255) NOT NULL,
    city                VARCHAR(100) NOT NULL,
    service_area        VARCHAR(150),
    latitude            DECIMAL(9,6),
    longitude           DECIMAL(9,6),
    id_document_type    VARCHAR(30),
    id_document_number  VARCHAR(50),
    id_document_path    VARCHAR(255),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                         CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

-- 3b) volunteers : extra details for users with role VOLUNTEER (needs verification)
CREATE TABLE IF NOT EXISTS volunteers (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    address             VARCHAR(255) NOT NULL,
    city                VARCHAR(100) NOT NULL,
    latitude            DECIMAL(9,6),
    longitude           DECIMAL(9,6),
    id_document_type    VARCHAR(30),
    id_document_number  VARCHAR(50),
    id_document_path    VARCHAR(255),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                         CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

-- 4) food_categories : type of food (cooked meals, bakery, ...)
CREATE TABLE IF NOT EXISTS food_categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 5) food_listings : surplus food posted by providers
CREATE TABLE IF NOT EXISTS food_listings (
    id              BIGSERIAL PRIMARY KEY,
    provider_id     BIGINT       NOT NULL REFERENCES food_providers (id) ON DELETE CASCADE,
    category_id     BIGINT       NOT NULL REFERENCES food_categories (id),
    title           VARCHAR(150) NOT NULL,
    description     TEXT,
    quantity        INTEGER      NOT NULL CHECK (quantity > 0),
    unit            VARCHAR(30)  NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    latitude        DECIMAL(9,6),
    longitude       DECIMAL(9,6),
    quality_tag     VARCHAR(20)  NOT NULL DEFAULT 'COOKED'
                    CHECK (quality_tag IN ('COOKED', 'FEW_HOURS_OLD', 'NEAR_EXPIRY')),
    estimated_beneficiaries INTEGER CHECK (estimated_beneficiaries IS NULL OR estimated_beneficiaries > 0),
    available_from  TIMESTAMP    NOT NULL,
    pickup_deadline TIMESTAMP    NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE'
                    CHECK (status IN ('AVAILABLE', 'CLAIMED', 'PICKED_UP', 'EXPIRED')),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (pickup_deadline > available_from)
);

-- 6) food_claims : an NGO/Volunteer requests a listing; provider accepts or rejects it
CREATE TABLE IF NOT EXISTS food_claims (
    id                BIGSERIAL PRIMARY KEY,
    listing_id        BIGINT      NOT NULL REFERENCES food_listings (id) ON DELETE CASCADE,
    claimant_user_id  BIGINT      NOT NULL REFERENCES users (id),
    distribution_plan TEXT        NOT NULL,
    claimed_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
<<<<<<< HEAD
    accepted_at       TIMESTAMP,
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED', 'CANCELLED')),
    distribution_proof_path VARCHAR(255),
    distribution_report     VARCHAR(1000),
    distributed_at          TIMESTAMP,
    note              VARCHAR(255)
);

-- 7) pickup_records : proof that the food was picked up
CREATE TABLE IF NOT EXISTS pickup_records (
    id           BIGSERIAL PRIMARY KEY,
    claim_id     BIGINT    NOT NULL UNIQUE REFERENCES food_claims (id) ON DELETE CASCADE,
    picked_up_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    picked_up_by VARCHAR(100),
    notes        VARCHAR(255)
);

-- 8) ratings : provider rates the NGO/Volunteer after a completed pickup
CREATE TABLE IF NOT EXISTS ratings (
    id             BIGSERIAL PRIMARY KEY,
    claim_id       BIGINT      NOT NULL UNIQUE REFERENCES food_claims (id) ON DELETE CASCADE,
    rated_user_id  BIGINT      NOT NULL REFERENCES users (id),
    rated_by_user_id BIGINT    NOT NULL REFERENCES users (id),
    rating_value   SMALLINT    NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    comment        VARCHAR(255),
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9) reports : "flag this" - a user reporting a scam, fake post, or misuse
CREATE TABLE IF NOT EXISTS reports (
    id               BIGSERIAL PRIMARY KEY,
    reporter_user_id BIGINT      NOT NULL REFERENCES users (id),
    listing_id       BIGINT      REFERENCES food_listings (id) ON DELETE CASCADE,
    claim_id         BIGINT      REFERENCES food_claims (id) ON DELETE CASCADE,
    reason           VARCHAR(500) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'OPEN'
                     CHECK (status IN ('OPEN', 'REVIEWED', 'DISMISSED')),
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (listing_id IS NOT NULL OR claim_id IS NOT NULL)
);

<<<<<<< HEAD
-- 10) events : an NGO's or Volunteer's upcoming distribution event/campaign,
--     so providers can see where food will be needed in advance
CREATE TABLE IF NOT EXISTS events (
    id           BIGSERIAL PRIMARY KEY,
    host_user_id BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title        VARCHAR(150) NOT NULL,
    event_date   TIMESTAMP    NOT NULL,
    location     VARCHAR(255) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    description  VARCHAR(1000),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11) notifications : simple in-app alerts (this project has no email/SMS/push
--     provider set up, so "nearby alert" means "shows up in this table" - see the
--     notes you were given on this simplification)
CREATE TABLE IF NOT EXISTS notifications (
    id                BIGSERIAL PRIMARY KEY,
    recipient_user_id BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message           VARCHAR(500) NOT NULL,
    link              VARCHAR(255),
    is_read           BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications (recipient_user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_events_city_date ON events (LOWER(city), event_date);

=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
-- Indexes to keep browsing, searching and claim lookups fast
CREATE INDEX IF NOT EXISTS idx_listings_status_deadline ON food_listings (status, pickup_deadline);
CREATE INDEX IF NOT EXISTS idx_listings_city ON food_listings (LOWER(city));
CREATE INDEX IF NOT EXISTS idx_claims_listing_status ON food_claims (listing_id, status);
CREATE INDEX IF NOT EXISTS idx_claims_claimant ON food_claims (claimant_user_id);
CREATE INDEX IF NOT EXISTS idx_ratings_rated_user ON ratings (rated_user_id);

-- Only ONE claim can be ACCEPTED per listing (this is what actually "wins" the food)
CREATE UNIQUE INDEX IF NOT EXISTS uq_accepted_claim_per_listing
    ON food_claims (listing_id) WHERE status = 'ACCEPTED';

-- The same NGO/Volunteer cannot send a second active request for the same listing
CREATE UNIQUE INDEX IF NOT EXISTS uq_active_request_per_claimant
    ON food_claims (listing_id, claimant_user_id) WHERE status IN ('PENDING', 'ACCEPTED');

-- Starter categories
INSERT INTO food_categories (name, description) VALUES
    ('Cooked meals', 'Rice, curry, biryani and other ready meals'),
    ('Bakery and bread', 'Bread, buns, cakes and pastries'),
    ('Fruits and vegetables', 'Fresh produce'),
    ('Packaged food', 'Sealed and packaged items'),
    ('Dairy and beverages', 'Milk, yogurt, juice and drinks'),
    ('Sweets and desserts', 'Mishti, pudding and other sweets'),
    ('Other', 'Anything else that is safe to eat')
ON CONFLICT (name) DO NOTHING;

-- NOTE: nobody can sign up as an admin through the website - that is intentional,
-- an admin account must never be self-service. To make yourself an admin for the demo,
-- register a normal account first, then run this in pgAdmin's Query Tool
-- (replace the email with your own):
--   UPDATE users SET is_admin = TRUE WHERE email = 'you@example.com';
