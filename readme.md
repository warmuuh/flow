Flow [![Build Status](https://travis-ci.org/warmuuh/flow.svg?branch=master)](https://travis-ci.org/warmuuh/flow) [![Maven Central](https://img.shields.io/maven-central/v/com.github.warmuuh/flow.svg)](https://mvnrepository.com/artifact/com.github.warmuuh/flow)
==========

Dataflow orchestration library. Stop worrying about how to execute your code or how to stitch together your futures. 
Just define which data-types you need and what you produce, the rest will be figured out by `flow` and you can concentrate fully on
your business logic.


Changelog:
 * 1.0.0 - initial release
 * 1.1.0 
    * async execution engine using CompletableFutures
    * supporting generic dependencies (e.g. `List<String>`)


Installation
-----

```xml
<dependency>
	<groupId>com.github.warmuuh</groupId>
	<artifactId>flow</artifactId>
	<version>...</version>
</dependency>
```

Usage
-----

Flow allows you to only define the input and output of your business logic. Depending on the required and provided dependencies, flow figures out what can be executed in 
parallel and what has to wait for other providers to finish. 

To setup flow and be ready to use it, you need to initialize it with the used contract as well as the used execution engine:

Included in flow is a `SequentialExecutionEngine`, which executes each necessary provider in a blocking order. Another provided execution engine is the `RxJavaExecutionEngine` which uses RxJava to execute all steps in parallel and stitches the results together so that the execution happens as parallel as possible.

Example for Sequential Execution:

```java
var flow = new Flow<>(new AnnotationContract(), new SequentialExecutionEngine<>());
flow.registerProviders(...);
var plan = flow.planExecution(new TypeRef(...), new TypeRef(InputObject.class));
ObjectRef result = flow.executePlan(plan, new InputObject());
```


Example for parallel execution using RxJava Execution-engine:

```java
var flow = new Flow<>(new AnnotationContract(), new RxJavaExecutionEngine<>());
flow.registerProviders(...);
var plan = flow.planExecution(new TypeRef(...), new TypeRef(InputObject.class));
Single<ObjectRef> result = flow.executePlan(plan, new InputObject());
```

As flow is very flexible, you can easily create your own contract for defining providers. A predefined contract is the AnnotationContract.


AnnotationContract
-----

To define your provider, you can use the `@Flower` annotation:

```java
public static class ExampleClass {
		@Flower
		public ProvidedType execute(RequiredType object) {
			...
		}
	}
```

Generics
-----
Parameterized types are normally removed due to type erasure. Only in certain cases are the types kept. Thats why to support generic types, a slightly different syntax is necessary:

```java
var plan = flow.planExecution(new GenericTypeRef<List<Double>>() {}, new GenericTypeRef<List<String>>() {});
List<String> strings = asList("0", "1", "2");
ObjectRef result = flow.executePlan(plan, new GenericObjectRef<List<String>>(strings) {});
```


More Details
-----
  * The execution plans are used to encode the steps that are necessary to provide the queried response. These plans are immutable and there is no need to recalculate those plans every time, they can be cached. Also this functionallity can be used in unit tests to verify that the registered providers are able to provide the required dependencies.
  * This framework can be easily integrated into e.g. Spring, just register all your necessary beans that contain providers into flow.

Note on Dataflow centric development
-----
* If you are used to imperative development, this will be slightly strange, as you have to encode all your logic into types. If there are several outcomes of your providers (e.g. error vs fetched result), you have to take care that the returned type is able to  represent both states.
* using dataflow centric development heavily improves your testability. (clearly defined responsibility, clear dependencies, all input/output is defined via types)
* providers work on their own. They dont actively "fetch" data except, if this is there only task
* providers stick to SRP (single responsibility principle), they should only do one thing. This leads to easy testability.

