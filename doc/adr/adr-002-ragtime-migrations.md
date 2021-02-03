# Ragtime migrations: Explicit listing of migrations in config.edn

## Status - Accepted

## Context

In some of our projects we configure ragtime migrations by explicitly defining
each individual migration like so:

```edn
{:duct.migrator/ragtime
 {:database   #ig/ref :duct.database/sql
  :logger     #ig/ref :duct/logger
  :strategy   :raise-error
  :migrations [#ig/ref :foo.migration/create-foo-table]}

 [:duct.migrator.ragtime/sql :foo.migration/create-foo-table]
 {:up   [#duct/resource "foo/migrations/create-foo-table.up.sql"]
  :down [#duct/resource "foo/migrations/create-foo-table.down.sql"]}}
```

Is other projects, however, we use the sugar to include all migrations found in
a path:

```edn
{:duct.migrator/ragtime
 {:database   #ig/ref :duct.database/sql
  :logger     #ig/ref :duct/logger
  :strategy   :raise-error
  :migrations [#ig/ref :foo.migrations/dev
               #ig/ref :foo.migrations/prod]}

 [:duct.migrator.ragtime/resources :foo.migrations/dev]
 {:path "dev/migrations"}

 [:duct.migrator.ragtime/resources :foo.migrations/prod]
 {:path "prod/migrations"}}
```

This is useful, yes. But it has some serious downsides:

1. It is harder to catch a conflict when 2 developers push new
   migrations. Especially if the conflict is in the ordering of the migrations
   only. While if we would be using explicit `:migrations []` listing then there
   will be obvious code conflicts.

2. There is a problem of partial changes being applied and the other few changes
   failing when a SQL sentence fails within a single migration file. On the
   other hand, doing explicit listing treats the whole migration file like a
   single transaction. So either all the SQL sentences in the migration file are
   applied, or all are rolled back.

There is an orthogonal issue that is related to this. The development migrations
always go at the beginning of the migrations list (or at the end, depending on
the order you specify in the `:migrations` key). But once you have at least one
production migration applied and you need to add a new for development migration
(or vice-versa, depending on the order specified in `:migrations`), it fails. It
expects to add the new development migration after the last development one (and
before any of the production ones) and it cannot. The reason why is that the
first production migration is already at that position.

Thus we need to make sure development migrations and production migrations are
not intertwined. One way to achieve this is by using separate ragtime migration
tables for each class of migrations. ragtime library let us specify the name of
the table where a given list of migrations will be recorded. By using different
table names for development and production we can keep the migrations separate
and guarantee the order of application in all cases.

In this case, as we will have more than one `:duct.migrator/ragtime`
configuration, we will need to use composite Integrant keys for the development
and production configurations.

We need to change `:duct.migrator/ragtime` in `config.edn` to:

```edn
{[:duct.migrator/ragtime :foo/prod]
 {:database   #ig/ref :duct.database/sql
  :logger     #ig/ref :duct/logger
  :strategy   :raise-error
  :migrations-table "ragtime_migrations"
  :migrations [#ig/ref :foo.migration/create-foo-table]}

 [:duct.migrator.ragtime/sql :foo.migration/create-foo-table]
 {:up   [#duct/resource "foo/migrations/create-foo-table.up.sql"]
  :down [#duct/resource "foo/migrations/create-foo-table.down.sql"]}}
```

and change `:duct.migrator/ragtime` in `dev.edn` to:

```edn
{[:duct.migrator/ragtime :foo/dev]
 {:database #ig/ref :duct.database/sql
  :logger #ig/ref :duct/logger
  :migrations-table "ragtime_migrations_dev"
  :fake-dependency-to-force-initialization-order #ig/ref [:duct.migrator/ragtime :foo/prod]}
  :migrations [#ig/ref :foo.dev-migration/create-dev-table]}

 [:duct.migrator.ragtime/sql :foo.dev-migration/create-dev-table]
 {:up   [#duct/resource "foo/dev_migrations/create-dev-table.up.sql"]
  :down [#duct/resource "foo/dev_migrations/create-dev-table.down.sql"]}
```

We also need to add a fake dependency in `[:duct.migrator/ragtime :foo/dev]`
(that `:duct.migration/ragtime` library completely ignores) to force the order
of application of the migrations. With the configuration shown above, production
migrations will always be applied before development ones.

## Decision

We will change the template to configure ragtime to use explicit listings of
migrations. The template will also configure two `:duct.migrator/ragtime`
Integrant keys, one for development and one for production. And set the order of
application of the migrations to production before development.

## Consequences

When using explicit listings of migrations, we generally use `#duct/resource` to
get the migration files. If we want to keep development and production migration
files in separate directories, we need to make sure we use different names for
the directories where we keep them. If we use `foo/migrations` in both cases,
`#duct/resource` doesn't know if that `migrations/create-foo-table.up.sql` is in
the development migrations directory, or the production migrations directory. It
will look for the migration file in one of them first, and if it does not find
it there, in the other one. But if we use the same migration file name in two
migrations, one for development and the other for production, we may end up
applying the wrong one.

The easiest way to avoid that is using different migrations directory names in
each case, like in the following examples. We use "migrations" as the directory
name in `config.edn`:

```edn
{[:duct.migrator/ragtime :foo/prod]
 {:database   #ig/ref :duct.database/sql
  :logger     #ig/ref :duct/logger
  :strategy   :raise-error
  :migrations-table "ragtime_migrations"
  :migrations [#ig/ref :foo.migration/create-foo-table]}

 [:duct.migrator.ragtime/sql :foo.migration/create-foo-table]
 {:up   [#duct/resource "foo/migrations/create-foo-table.up.sql"]
  :down [#duct/resource "foo/migrations/create-foo-table.down.sql"]}}
```

and "dev_migrations" as the directory name in `dev.edn`:

```edn
{[:duct.migrator/ragtime :foo/dev]
 {:database #ig/ref :duct.database/sql
  :logger #ig/ref :duct/logger
  :migrations-table "ragtime_migrations_dev"
  :fake-dependency-to-force-initialization-order #ig/ref [:duct.migrator/ragtime :foo/prod]}
  :migrations [#ig/ref :foo.dev-migration/create-foo-table]}

 [:duct.migrator.ragtime/sql :foo.dev-migration/create-foo-table]
 {:up   [#duct/resource "foo/dev_migrations/create-foo-table.up.sql"]
  :down [#duct/resource "foo/dev_migrations/create-foo-table.down.sql"]}
```
