# Architecture

## Overview

Orbit is a self-hosted distributed job scheduler.

The system allows users to define jobs that can be executed automatically according to a schedule or manually on demand.

Each execution of a job is recorded independently, allowing Orbit to keep a history of what ran, when it ran, and whether it succeeded or failed.

In future versions, jobs will be executed by workers running on different machines.

## Core Concepts

### Job

A Job describes a task that Orbit knows how to execute.

A job defines information such as:

* name;
* command;
* schedule;
* whether the job is enabled;
* maximum number of retries;
* execution timeout.

A Job does not represent a specific execution.

The same Job can generate many Executions over time.

Example:

```text
database-backup
./backup.sh
every day at 03:00
```

### Execution

An Execution represents one concrete run of a Job.

For example, if `database-backup` runs every day for thirty days, Orbit still has one Job but thirty Executions.

An Execution initially has one of the following states:

* `QUEUED`
* `RUNNING`
* `SUCCEEDED`
* `FAILED`
* `CANCELLED`

Executions will also store information such as when the execution started, when it finished, and its attempt number.

### Worker

A Worker represents a machine capable of executing Jobs.

Workers are not part of the first implementation of Orbit.

In a future version, a Java worker application will connect to the Orbit server, receive executions and report their results.

Examples of workers could be:

```text
fedora-desktop
macbook-air
homelab-server
```

Initially, a worker can be considered either:

* `ONLINE`
* `OFFLINE`

The exact mechanism used to determine worker availability will be defined when worker communication is implemented.

## Relationships

A Job can generate many Executions.

An Execution belongs to exactly one Job.

In future versions, an Execution may be assigned to a Worker.

```text
Job
 │
 │ generates
 ▼
Execution
 │
 │ assigned to
 ▼
Worker
```

## Initial Architecture

The first version of Orbit will contain three main components:

```text
orbit-web
Angular frontend

      │
      │ HTTP
      ▼

orbit-server
Spring Boot application

      │
      ▼

PostgreSQL
```

A Java worker will be introduced later:

```text
orbit-worker
Java application
```

The initial development will focus on the relationship between Jobs and Executions before distributed workers are introduced.

## Initial Scope

The first implementation should support:

* creating Jobs;
* listing Jobs;
* updating Jobs;
* deleting Jobs;
* creating Executions;
* storing Execution history.

The following features are intentionally outside the initial scope:

* distributed workers;
* worker heartbeats;
* retries;
* realtime logs;
* WebSockets;
* authentication;
* permissions;
* distributed locking;
* job dependencies.

These features will be introduced gradually as the project evolves.
