package com.global_water_polo.play_abroad.core.utils.networking

/**
    Networking extensions
    map -> In case the API request is successful we need to map the response (DTO) to UI ready object (model)
    onSuccess() and onError() are used so the VMs are a bit tidier (avoiding the when block to check the response
    in VM)
 **/

inline fun <T, E: Error, M: String?, R> Resource<T, E, M>.map(map: (T) -> R): Resource<R, E, M> {
    return when(this) {
        is Resource.Error -> Resource.Error(error, message)
        is Resource.Success -> Resource.Success(map(data))
    }
}

inline fun <T, E: Error, M: String?> Resource<T, E, M>.onSuccess(action: (T) -> Unit): Resource<T, E, M> {
    return when(this) {
        is Resource.Error -> this
        is Resource.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E: Error> Resource<T, E, String?>.onError(action: (E, String?) -> Unit): Resource<T, E, String?> {
    return when(this) {
        is Resource.Error -> {
            action(error, message)
            this
        }
        is Resource.Success -> this
    }
}
