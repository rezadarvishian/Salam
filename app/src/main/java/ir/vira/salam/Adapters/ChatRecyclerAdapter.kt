package ir.vira.salam.Adapters


import ir.vira.salam.Models.MessageModel
import ir.vira.network.NetworkInformation
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import ir.vira.salam.R
import androidx.recyclerview.widget.RecyclerView
import ir.vira.salam.Enumerations.MessageType
import ir.vira.salam.core.BaseViewHolder
import ir.vira.salam.databinding.OtherMessageBinding
import ir.vira.salam.databinding.OwnMessageBinding

class ChatRecyclerAdapter(
    private val messageModels: MutableList<MessageModel>,
    private val networkInformation : NetworkInformation
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return if (messageModels[position].userModel.ip == networkInformation.ipAddress) MessageType.OWN.ordinal else MessageType.OTHER.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MessageType.OWN.ordinal) {
            OwnMessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.own_messages_item, parent, false))
        } else {
            OtherMessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.others_messages_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is OwnMessageHolder){
            val message = messageModels[position]
            if (message.userModel.profile.height != message.userModel.profile.width){
                holder.binding.chatImageProfile.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            holder.binding.chatImageProfile.setImageBitmap(message.userModel.profile)
            holder.binding.chatTextName.text = message.text
            holder.binding.chatTextText.text = message.userModel.name
        }else if (holder is OtherMessageHolder){
            val message = messageModels[position]
            if (message.userModel.profile.height != message.userModel.profile.width){
                holder.binding.chatImageProfile.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            holder.binding.chatImageProfile.setImageBitmap(message.userModel.profile)
            holder.binding.chatTextName.text = message.text
            holder.binding.chatTextText.text = message.userModel.name
        }



    }

    override fun getItemCount() = messageModels.size


    fun newMsg(messageModel: MessageModel) {
        messageModels.add(0, messageModel)
        notifyItemInserted(0)
    }

}

class OwnMessageHolder(itemView: View) : BaseViewHolder<OwnMessageBinding>(itemView)
class OtherMessageHolder(itemView: View) : BaseViewHolder<OtherMessageBinding>(itemView)
