# RoomInspector 
An in-app database inspector for [Android Room](https://developer.android.com/topic/libraries/architecture/room) databases.

[ ![Download](https://api.bintray.com/packages/ktvipin27/RoomInspector/com.ktvipin.roominspector/images/download.svg) ](https://bintray.com/ktvipin27/RoomInspector/com.ktvipin.roominspector/_latestVersion)

<img src="https://github.com/ktvipin27/RoomInspector/blob/master/preview/preview.gif?raw=true" width="240" height="480" />

Built with ❤︎ by [Vipin KT](https://twitter.com/ktvipin27)

## Features

* View tables
* Add rows
* Update rows
* Delete rows
* Delete tables
* Drop tables
* Custom queries

## Installation

Add this in your app's build.gradle file:

<details open>
<summary>Groovy</summary>
  
```groovy
  dependencies {
       implementation 'com.ktvipin:roominspector:1.0.0'
  }
```

</details>
<details open>
<summary>Kotlin</summary>
  
```kotlin
  dependencies {
       implementation("com.ktvipin:roominspector:1.0.0")
  }
```

</details>

## Usage

To inspect your Room database with RoomInspector, just call the `inspect()` method by passing your database class and database name.

```kotlin
   RoomInspector.inspect(context, MyDatabase::class.java, MyDatabase.DB_NAME)
```

## License    

    Copyright 2020 Vipin KT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
