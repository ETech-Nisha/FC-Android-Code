
open class BaseModel {

    var isSuccess = false
    var errorCode: String = ""
    var errorMessage: String = ""
    var cause: String = ""


    constructor() {
        this.isSuccess = false
        this.errorCode = ""
        this.errorMessage = ""
        this.cause = ""
    }

   /* constructor(message: String) {
        this.message = message
    }*/

    constructor(isSuccess: Boolean, errorCode: String, errorMessage: String, cause: String){
        this.isSuccess = isSuccess
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        this.cause = cause
    }
}
